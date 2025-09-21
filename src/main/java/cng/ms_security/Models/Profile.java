package cng.ms_security.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Profile {

}
// con el interceptor se mira las peticiones con el token el autorizador mira el token de ahi se extrae el id
//  de usuario de ahi el s¿rol que posee y de ahi los permisos