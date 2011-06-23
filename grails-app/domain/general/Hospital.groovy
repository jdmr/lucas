package general

class Hospital implements Serializable {
    String nombre
    String nombreCompleto

    static constraints = {
        nombre blank:false, maxSize:64, unique:true
        nombreCompleto blank:false, maxSize:128
    }

    static mapping = {
        table 'hospitales'
    }

    String toString() {
        return nombre
    }
}
