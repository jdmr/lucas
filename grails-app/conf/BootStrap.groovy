class BootStrap {

    def springSecurityService

    def init = { servletContext ->
        log.info "Validando roles"
        def rolAdmin = general.Rol.findByAuthority('ROLE_ADMIN')
        if (general.Rol.count() != 3) {
            if (!rolAdmin) {
                rolAdmin = new general.Rol(authority: 'ROLE_ADMIN').save(flush:true)
            }
            def rolDoc = general.Rol.findByAuthority('ROLE_DOCTOR')
            if (!rolDoc) {
                rolDoc = new general.Rol(authority: 'ROLE_DOCTOR').save(flush:true)
            }
            def rolPat = general.Rol.findByAuthority('ROLE_PATIENT')
            if (!rolPat) {
                rolPat = new general.Rol(authority: 'ROLE_PATIENT').save(flush:true)
            }
        }

        log.info "Validando usuarios"
        def admin = general.UsuarioRol.findByRol(rolAdmin)
        if (!admin) {
            def password = springSecurityService.encodePassword('admin')
            admin = new general.Usuario(
                username:'admin'
                ,password:password
                ,nombre:'David'
                ,apellido:'Mendoza'
                ,correo:'david.mendoza@um.edu.mx'
            )
            admin.save(flush:true)
            general.UsuarioRol.create(admin, rolAdmin, true)
        }

        log.info("Aplicacion inicializada")
    }
    def destroy = {
    }
}
