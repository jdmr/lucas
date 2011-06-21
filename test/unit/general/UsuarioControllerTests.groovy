package general



import org.junit.*
import grails.test.mixin.*


@TestFor(UsuarioController)
@Mock(Usuario)
class UsuarioControllerTests {


    @Test
    void testIndex() {
        controller.index()
        assert "/usuario/list" == response.redirectedUrl
    }

    @Test
    void testList() {

        def model = controller.list()

        assert model.usuarioInstanceList.size() == 0
        assert model.usuarioInstanceTotal == 0

    }

    @Test
    void testCreate() {
       def model = controller.create()

       assert model.usuarioInstance != null


    }

    @Test
    void testSave() {
        controller.save()

        assert model.usuarioInstance != null
        assert view == '/usuario/create'

        // TODO: Populate valid properties

        controller.save()

        assert response.redirectedUrl == '/usuario/show/1'
        assert controller.flash.message != null
        assert Usuario.count() == 1
    }


    @Test
    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/usuario/list'


        def usuario = new Usuario()

        // TODO: populate domain properties

        assert usuario.save() != null

        params.id = usuario.id

        def model = controller.show()

        assert model.usuarioInstance == usuario
    }

    @Test
    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/usuario/list'


        def usuario = new Usuario()

        // TODO: populate valid domain properties

        assert usuario.save() != null

        params.id = usuario.id

        def model = controller.edit()

        assert model.usuarioInstance == usuario
    }

    @Test
    void testUpdate() {

        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/usuario/list'

        response.reset()


        def usuario = new Usuario()

        // TODO: populate valid domain properties

        assert usuario.save() != null

        // test invalid parameters in update
        params.id = usuario.id

        controller.update()

        assert view == "/usuario/edit"
        assert model.usuarioInstance != null

        usuario.clearErrors()

        // TODO: populate valid domain form parameter
        controller.update()

        assert response.redirectedUrl == "/usuario/show/$usuario.id"
        assert flash.message != null
    }

    @Test
    void testDelete() {
        controller.delete()

        assert flash.message != null
        assert response.redirectedUrl == '/usuario/list'

        response.reset()

        def usuario = new Usuario()

        // TODO: populate valid domain properties
        assert usuario.save() != null
        assert Usuario.count() == 1

        params.id = usuario.id

        controller.delete()

        assert Usuario.count() == 0
        assert Usuario.get(usuario.id) == null
        assert response.redirectedUrl == '/usuario/list'


    }


}