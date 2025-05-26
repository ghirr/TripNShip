package org.Esprit.TripNShip.Utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import io.github.cdimascio.dotenv.Dotenv;


public class TwilioSMS {

        private static final Dotenv dotenv = Dotenv.load();
        private static final String ACCOUNT_SID =  dotenv.get("TWILIO_ACCOUNT_SID");
        private static final String AUTH_TOKEN = dotenv.get("TWILIO_AUTH_TOKEN");
        private static final String TWILIO_PHONE_NUMBER = "+19407163789";

    public static boolean sendSMSConfirmationCode(String phoneNumber, int code) {
        try {
            System.out.println(phoneNumber);
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

            Message message = Message.creator(
                    new PhoneNumber("+216"+phoneNumber),
                    new PhoneNumber(TWILIO_PHONE_NUMBER),
                    "Votre code de vérification est : " + code
            ).create();

            System.out.println("📱 SMS envoyé avec succès : " + message.getSid());
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erreur SMS : " + e.getMessage());
            return false;
        }
    }
}