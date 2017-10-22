package basic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * Creado por  Ascari Q. Romo Pedraza - molder.itp@gmail.com on 20/03/17.
 */
@Component
public class Cliente {

    private long id;
    private int diaPago;
    private double abono;

    private String celular;
    private String twitter;
    private String email;

    private MedioContacto medioContacto;

    @Autowired
    private Recordatorio recordatorioEmail;

    @Autowired
    private Recordatorio recordatorioSms;

    @Autowired
    private Recordatorio recordatorioTwitter;


    public void enviarRecordatorio() {
        if (abono >= 200 && abono <= 300) {
            recordatorioEmail.nextHandler(recordatorioSms);
            recordatorioSms.nextHandler(recordatorioTwitter);

            recordatorioEmail.enviar(this);
        }
    }

    public Recordatorio getRecordatorioEmail() {
        return recordatorioEmail;
    }

    public void setRecordatorioEmail(Recordatorio recordatorioEmail) {
        this.recordatorioEmail = recordatorioEmail;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDiaPago() {
        return diaPago;
    }

    public void setDiaPago(int diaPago) {
        this.diaPago = diaPago;
    }

    public double getAbono() {
        return abono;
    }

    public void setAbono(double abono) {
        this.abono = abono;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MedioContacto getMedioContacto() {
        return medioContacto;
    }

    public void setMedioContacto(MedioContacto medioContacto) {
        this.medioContacto = medioContacto;
    }

}
