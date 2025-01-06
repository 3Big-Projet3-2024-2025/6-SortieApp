import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:sortie_app_frontend/pages/student_management_page.dart';
import 'package:sortie_app_frontend/pages/supervisor_management_page.dart';

class localAdminHomePage extends StatelessWidget {
  // Secure storage instance to handle session data
  final secureStorage = const FlutterSecureStorage();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        // Navy blue background color
        backgroundColor: const Color(0xFF0052CC),
        title: const Text(
          'Sortie\'App',
          style: TextStyle(
            color: Colors.white,   // White text
            fontWeight: FontWeight.bold,
          ),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout, color: Colors.white),
            // Logs out the user and navigates to the login page
            onPressed: () async {
              await secureStorage.deleteAll();
              Get.offNamed('/login');
            },
          ),
        ],
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Displays the SortieApp logo
            Image.asset(
              'assets/images/SortieApp_logo_no_bg.png',
              width: 200,
              height: 200,
            ),
            // "Welcome to Sortie'App" text
            const Padding(
              padding: EdgeInsets.symmetric(vertical: 20.0),
              child: Text(
                'Welcome to Sortie\'App',
                style: TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                ),
                textAlign: TextAlign.center,
              ),
            ),
            // Button to navigate to student management page
            ElevatedButton(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => const student_management_page(),
                  ),
                );
              },
              child: const Text('Manage students'),
            ),
            // Button to navigate to supervisor management page
            ElevatedButton(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => const Supervisors_management_page(),
                  ),
                );
              },
              child: const Text('Manage supervisors'),
            ),
          ],
        ),
      ),
    );
  }
}
