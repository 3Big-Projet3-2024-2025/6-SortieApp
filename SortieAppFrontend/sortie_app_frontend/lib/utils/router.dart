import 'package:get/get.dart';
import 'package:jwt_decoder/jwt_decoder.dart';
import 'package:sortie_app_frontend/utils/tokenUtils.dart';


Future<void> redirectHome() async {
  final accesToken= await getAccesToken();

  if(accesToken != null) {
    Map<String, dynamic> decodedToken = JwtDecoder.decode(accesToken);
    String authority = "";
    if (decodedToken.containsKey("roles") && decodedToken["roles"] is List &&
        decodedToken["roles"].isNotEmpty) {
      var roles = decodedToken["roles"] as List;
      if (roles[0] is Map && roles[0].containsKey("authority")) {
        authority = roles[0]["authority"];
        switch (authority) {
          case "ROLE_ADMIN":
            Get.offNamed('/adminHome');
            break;
          case "ROLE_LOCAL_ADMIN":
            Get.offNamed('/localAdminHome');
            break;
          case "ROLE_RESPONSIBLE":
            Get.offNamed('/studentList');
            break;
          case "ROLE_SUPERVISOR":
            Get.offNamed('/supervisorHome');
            break;
          case "ROLE_STUDENT":
            Get.offNamed('/studentHome');
            break;
          default:
            Get.snackbar('Redirect failed', 'Unknow role');
            Get.offNamed('/login');
            deleteTokens();
        }
      } else {
        Get.snackbar('Redirect failed', 'Can\'t find the roles');
        Get.offNamed('/login');
        deleteTokens();
      }
    } else {
      Get.snackbar('Redirect failed', 'Can\'t find the authority');
      Get.offNamed('/login');
      deleteTokens();
    }
  } else {
    Get.snackbar('Redirect failed', 'There is no token');
    Get.offNamed('/login');
    deleteTokens();
  }
}
