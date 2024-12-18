import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:sortie_app_frontend/pages/login_page.dart';
import 'package:sortie_app_frontend/pages/admin_home_page.dart';
import 'package:sortie_app_frontend/pages/user_crud_page.dart';
import 'package:sortie_app_frontend/pages/schools_crud_page.dart';
import 'package:sortie_app_frontend/pages/autorisation_crud_page.dart';

void main() {
  runApp(SortieApp());
}

class SortieApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'SortieApp',
      initialRoute: '/login',
      getPages: [
        GetPage(name: '/login', page: () => loginPage()),
        GetPage(name: '/adminHome', page: () => adminHomePage()),
        GetPage(name: '/users', page: () => UserApp()),
        GetPage(name: '/schools', page: () => SchoolsCrud()),
        GetPage(name: '/autorisations', page: () => AutorisationCrudPage()),
      ],
    );
  }
}//john.doe@example.com