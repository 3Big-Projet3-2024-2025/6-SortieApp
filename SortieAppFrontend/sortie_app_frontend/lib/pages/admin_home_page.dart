import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class adminHomePage extends StatelessWidget {
  final secureStorage = const FlutterSecureStorage();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF7F9FC), // Couleur de fond gris clair
      appBar: AppBar(
        backgroundColor: const Color(0xFF0052CC), // Bleu marine
        title: const Text(
          'Sortie\'App',
          style: TextStyle(
            color: Colors.white, // Titre en blanc
            fontWeight: FontWeight.bold,
          ),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout, color: Colors.white), // Icône blanche
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
            // Logo de l'application
            Image.asset(
              'assets/images/SortieApp_logo_no_bg.png',
              height: 120, // Taille du logo
            ),
            const SizedBox(height: 32),

            // Message de bienvenue
            const Padding(
              padding: EdgeInsets.symmetric(vertical: 20.0),
              child: Text(
                'Welcome to Sortie\'App',
                style: TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                  color: Color(0xFF0052CC), // Texte en bleu marine
                ),
                textAlign: TextAlign.center,
              ),
            ),

            // Bouton "Users Management"
            ElevatedButton(
              onPressed: () {
                Get.offNamed('/users');
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF0052CC), // Bleu marine
                padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 16),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
              child: const Text(
                'Users Management',
                style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
              ),
            ),
            const SizedBox(height: 20),

            // Bouton "Schools Management"
            ElevatedButton(
              onPressed: () {
                Get.offNamed('/schools');
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF0052CC), // Bleu marine
                padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 16),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
              child: const Text(
                'Schools Management',
                style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
              ),
            ),
            const SizedBox(height: 20),

            // Bouton "Autorisations Management"
            ElevatedButton(
              onPressed: () {
                Get.offNamed('/autorisations');
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF0052CC), // Bleu marine
                padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 16),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
              child: const Text(
                'Autorisations Management',
                style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
