package general

import org.apache.commons.lang.builder.HashCodeBuilder

class UsuarioHospital implements Serializable {
    Usuario usuario
    Hospital hospital
    String numero
    Date dateCreated

    boolean equals(other) {
        if (!(other instanceof UsuarioHospital)) {
            return false
        }

        other.usuario?.id == usuario?.id &&
        other.hospital?.id == hospital?.id
    }

	int hashCode() {
		def builder = new HashCodeBuilder()
		if (usuario) builder.append(usuario.id)
		if (hospital) builder.append(hospital.id)
		builder.toHashCode()
	}

    static mapping = {
        id composite: ['hospital', 'usuario']
        version false
    }

    static UsuarioHospital get(long usuarioId, long hospitalId) {
        find 'from UsuarioHospital where usuario.id = :usuarioId and hospital.id = :hospitalId', [usuarioId: usuarioId, hospitalId: hospitalId]
    }

    static UsuarioHospital create(Usuario usuario, Hospital hospital, String numero boolean flush = false) {
        new UsuarioHospital(usuario: usuario, hospital: hospital, numero: numero).save(flush: flush, insert: true)
    }

    static boolean remove(Usuario usuario, Hospital hospital, boolean flush = false) {
        UsuarioHospital instance = UsuarioHospital.findByUsuarioAndHospital(usuario, hospital)
        instance ? instance.delete(flush: flush) : false
    }

    static void removeAll(Usuario usuario) {
        executeUpdate 'DELETE FROM UsuarioHospital WHERE usuario = : usuario', [usuario: usuario]
    }

    static void removeAll(Hospital hospital) {
        executeUpdate 'DELETE FROM UsuarioHospital WHERE hospital = :hospital', [hospital: hospital]
    }

}
