import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class ResponsibleHomePage extends StatelessWidget {
  final secureStorage = const FlutterSecureStorage();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: const Text(
            'Sortie\'App',
            style: TextStyle(
              color: Colors.white, // Titre en blanc
              fontWeight: FontWeight.bold,
            ),
          ),
          backgroundColor: Color(0xFF0052CC), // Bleu marine
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
              ElevatedButton(
                onPressed: () {
                  Get.offNamed('/studentList');
                },
                child: const Text('Students Management'),
              ),
              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: () {
                  Get.offNamed('/schools');
                },
                child: const Text('Schools Management'),
              ),
              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: () {
                  Get.offNamed('/autorisations');
                },
                child: const Text('Autorisations management'),
              ),
            ],
          ),
        )
    );
  }
}