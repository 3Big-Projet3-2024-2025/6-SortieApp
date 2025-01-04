import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:sortie_app_frontend/pages/student_management_page.dart';
import 'package:sortie_app_frontend/pages/supervisor_management_page.dart';

class localAdminHomePage extends StatelessWidget {
  final secureStorage = const FlutterSecureStorage();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          backgroundColor: Color(0xFF87CEEB),
          title: const Text('Sortie\'App'),
          actions: [
            IconButton(
              icon: const Icon(Icons.logout),
              onPressed: () async {
                await secureStorage.deleteAll();
                Get.offNamed('/login');
              },
            ),
          ],
        ),
        body:
        Center (
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
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
              ElevatedButton(
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => const student_management_page()),
                  );
                },
                child: const Text('Manage students'),
              ),
              ElevatedButton(
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => const Supervisors_management_page()),
                  );
                },
                child: const Text('Manage supervisors'),
              ),
            ],
          ),
        )
    );
  }
}