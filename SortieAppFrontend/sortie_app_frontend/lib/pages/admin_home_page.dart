import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class adminHomePage extends StatelessWidget {
  final secureStorage = const FlutterSecureStorage();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          backgroundColor: Color(0xFF87CEEB),
          title: const Text('Home'),
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
                  Get.offNamed('/users');
                },
                child: const Text('User Management'),
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