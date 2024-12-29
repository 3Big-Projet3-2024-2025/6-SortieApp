import 'package:http/http.dart' as http;
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'dart:convert';
import 'package:get/get.dart';


class LoginController extends GetxController {
  final secureStorage = const FlutterSecureStorage();


  var email = ''.obs;
  var password = ''.obs;

  var isLoading = false.obs;


  Future<void> login() async {
    if(email.value.isEmpty || password.value.isEmpty ) {
      Get.snackbar('Error', 'Please fill in all fields');
    }

    isLoading(true);


    final uri = Uri.parse('http://10.0.2.2:8081/auth/login');

    final request = http.MultipartRequest('POST', uri);
    request.fields['email'] = email.value;
    request.fields['password'] = password.value;

    try {
      final response = await http.Response.fromStream(await request.send());
      if (response.statusCode == 200) {
        final responseData = jsonDecode(response.body);

        final String accesToken = responseData['accesToken'];
        final String refreshToken = responseData['refreshToken'];

        await secureStorage.write(key: 'accesToken', value: accesToken);
        await secureStorage.write(key: 'refreshToken', value: refreshToken);
        Get.offNamed('/home');
      } else {
        Get.snackbar('Login Failed', 'Invalid credentials');
      }
    } catch (e) {
      Get.snackbar('Error', e.toString());
    } finally {
      isLoading(false);
    }
  }
}