import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:sortie_app_frontend/pages/qrcode_page.dart';

class studentHomePage extends StatelessWidget {
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
                  Get.to(() => const QRCodePage());
                },
                child: const Text('QR code'),
              ),

            ],
          ),
        )
    );
  }
}