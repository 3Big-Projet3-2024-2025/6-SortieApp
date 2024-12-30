import 'package:flutter_secure_storage/flutter_secure_storage.dart';


final secureStorage = const FlutterSecureStorage();

Future<String?> getAccesToken() async {
  return await secureStorage.read(key: 'accesToken');
}

Future<String?> getRefreshToken() async {
  return await secureStorage.read(key: 'refreshToken');
}

void deleteTokens(){
  secureStorage.delete(key: 'accesToken');
  secureStorage.delete(key: 'refreshToken');
}