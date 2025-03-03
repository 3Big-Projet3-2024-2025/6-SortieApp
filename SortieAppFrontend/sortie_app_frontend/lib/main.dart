import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:sortie_app_frontend/pages/QRcode_scanner.dart';
import 'package:sortie_app_frontend/pages/local_admin_home_page.dart';
import 'package:sortie_app_frontend/pages/login_page.dart';
import 'package:sortie_app_frontend/pages/admin_home_page.dart';
import 'package:sortie_app_frontend/pages/qrcode_page.dart';
import 'package:sortie_app_frontend/pages/student_home_page.dart';
import 'package:sortie_app_frontend/pages/responsible_home_page.dart';
import 'package:sortie_app_frontend/pages/student_school_list.dart';
import 'package:sortie_app_frontend/pages/user_crud_page.dart';
import 'package:sortie_app_frontend/pages/school_crud_page.dart';
import 'package:sortie_app_frontend/pages/autorisation_crud_page.dart';
import 'package:sortie_app_frontend/pages/my_profile_page.dart';

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
        //GetPage(name: '/studentHome', page: () => studentHomePage()),
        GetPage(name: '/users', page: () => UserApp()),
        GetPage(name: '/schools', page: () => SchoolsCrud()),
        GetPage(name: '/autorisations', page: () => AutorisationCrudPage()),
        GetPage(name: '/responsibleHome', page: () => ResponsibleHomePage()),
        GetPage(name: '/studentHome', page: () => MyProfile()), // Ajout de la route MyProfile
        GetPage(name: '/studentList', page: () => StudentListPage()),
        GetPage(name: '/qrcode', page: () => const QRCodePage()),
        GetPage(name: '/supervisorHome', page: () => QRScannerPage()),
        GetPage(name: '/localAdminHome', page: () => localAdminHomePage())
      ],
    );
  }
}
