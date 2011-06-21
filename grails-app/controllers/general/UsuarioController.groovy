package general

import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

@Secured(['ROLE_HOSPITAL','ROLE_DOCTOR'])
class UsuarioController {

    def springSecurityService

    static allowedMethods = [crea: "POST", actualiza: "POST", elimina: "POST"]

    def index = {
        redirect(action: "lista", params: params)
    }

	def lista = {
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		[usuarios: Usuario.list(params), totalDeUsuarios: Usuario.count()]
	}

    def nuevo = {
        def usuario = new Usuario()
        usuario.properties = params

        def roles = obtieneListaDeRoles(null)

        return [usuario: usuario, roles: roles]
    }

    def crea = {
        Usuario.withTransaction {
            def usuario = new Usuario(params)
            usuario.password = springSecurityService.encodePassword(params.password)
            def currentUser = springSecurityService.currentUser
            usuario.empresa = currentUser.empresa
            if (usuario.save(flush: true)) {
                def roles = [] as Set
                if (params.ROLE_ADMIN) {
                    roles << Rol.findByAuthority('ROLE_ADMIN')
                } 
                if (params.ROLE_HOSPITAL) {
                    roles << Rol.findByAuthority('ROLE_HOSPITAL')
                } 
                if (params.ROLE_DOCTOR) {
                    roles << Rol.findByAuthority('ROLE_DOCTOR')
                } 
                roles << Rol.findByAuthority('ROLE_PATIENT')
                for(rol in roles) {
                    UsuarioRol.create(usuario, rol, false)
                }
                flash.message = message(code: 'default.created.message', args: [message(code: 'usuario.label', default: 'Usuario'), usuario.username])
                redirect(action: "ver", id: usuario.id)
            } else {
                log.error("Hubo un error al crear el usuario ${usuario.errors}")
                render(view: "nuevo", model: [usuario: usuario])
            }
        }
    }

    def ver = {
        def usuario = Usuario.get(params.id)
        if (!usuario) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'usuario.label', default: 'Usuario'), params.id])
            redirect(action: "lista")
        }
        else {
            def roles = obtieneListaDeRoles(usuario)

            return [usuario: usuario, roles: roles]
        }
    }

    def edita = {
        def usuario = Usuario.get(params.id)
        if (!usuario) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'usuario.label', default: 'Usuario'), params.id])
            redirect(action: "lista")
        }
        else {
            def roles = obtieneListaDeRoles(usuario)

            return [usuario: usuario, roles: roles]
        }
    }

    def actualiza = {
        Usuario.withTransaction {
            def usuario = Usuario.get(params.id)
            if (usuario) {
                if (params.version) {
                    def version = params.version.toLong()
                    if (usuario.version > version) {
                        
                        usuario.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'usuario.label', default: 'Usuario')] as Object[], "Another user has updated this Usuario while you were editing")
                        render(view: "edita", model: [usuario: usuario])
                        return
                    }
                }
                if (usuario.password != params.password) {
                    usuario.password = springSecurityService.encodePassword(params.password)
                }
                params.remove('password')
                usuario.properties = params
                def currentUser = springSecurityService.currentUser
                usuario.empresa = currentUser.empresa
                if (!usuario.hasErrors() && usuario.save(flush: true)) {
                    def roles = [] as Set
                    if (params.ROLE_ADMIN) {
                        roles << Rol.findByAuthority('ROLE_ADMIN')
                    } 
                    if (params.ROLE_HOSPITAL) {
                        roles << Rol.findByAuthority('ROLE_HOSPITAL')
                    } 
                    if (params.ROLE_DOCTOR) {
                        roles << Rol.findByAuthority('ROLE_DOCTOR')
                    } 
                    roles << Rol.findByAuthority('ROLE_PATIENT')
                    for(rol in roles) {
                        UsuarioRol.create(usuario, rol, false)
                    }
                    UsuarioRol.removeAll(usuario)
                    for(rol in roles) {
                        UsuarioRol.create(usuario, rol, false)
                    }
                    flash.message = message(code: 'default.updated.message', args: [message(code: 'usuario.label', default: 'Usuario'), usuario.username])
                    redirect(action: "ver", id: usuario.id)
                }
                else {
                    render(view: "edita", model: [usuario: usuario])
                }
            }
            else {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'usuario.label', default: 'Usuario'), params.id])
                redirect(action: "lista")
            }
        }
    }

    def elimina = {
        def usuario = Usuario.get(params.id)
        if (usuario) {
            try {
                def nombre = usuario.username
                UsuarioRol.removeAll(usuario)
                usuario.delete(flush: true)
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'usuario.label', default: 'Usuario'), nombre])
                redirect(action: "lista")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'usuario.label', default: 'Usuario'), params.id])
                redirect(action: "ver", id: params.id)
            }
        }
        else {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'usuario.label', default: 'Usuario'), params.id])
            redirect(action: "lista")
        }
    }

    def obtieneListaDeRoles = { usuario ->
        def roles = Rol.list()

        roles.sort { r1, r2 ->
            r1.authority <=> r2.authority
        }
        Set userRoleNames = []
        for (role in usuario?.authorities) {
            userRoleNames << role.authority
        }
        LinkedHashMap<Rol, Boolean> roleMap = [:]
        for (role in roles) {
            roleMap[(role)] = userRoleNames.contains(role.authority)
        }
        return roleMap
    }

}
