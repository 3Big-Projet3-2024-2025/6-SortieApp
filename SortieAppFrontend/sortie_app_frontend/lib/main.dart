import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:sortie_app_frontend/pages/login_page.dart';
import 'package:sortie_app_frontend/pages/home_page.dart';
import 'package:sortie_app_frontend/pages/my_profile_page.dart';
import 'package:sortie_app_frontend/pages/school_crud_page.dart';

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
        GetPage(name: '/home', page: () => homePage()),
        GetPage(name: '/myProfile', page: () => MyProfile()),
        GetPage(name: '/school', page: () => SchoolsCrud()),// Ajout de la route MyProfile
      ],
    );
  }
}
