import basic.ApplicationConfig
import basic.Cliente
import basic.Recordatorio
import basic.RecordatorioEmail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 20/03/17.
 */
@ContextConfiguration(classes = ApplicationConfig.class)
class RecordatorioSpec extends Specification{

    @Autowired
    Cliente cliente

    RecordatorioEmail recordatorioEmail = Mock()

    def setup(){
        cliente.setAbono(280);
        cliente.setDiaPago(7); //1 Domingo, 2 Lunes, 3 Martes, etc.
        cliente.setEmail("molder.itp@gmail.com");
        cliente.setTwitter("@windoctor");
        cliente.setCelular("0123456789");
        cliente.recordatorioEmail = recordatorioEmail
    }

    def 'enviar recordatorio de pago a clientes'(){
        when:
            cliente.enviarRecordatorio()
        then:
            1 * recordatorioEmail.enviar(cliente);
    }

}
