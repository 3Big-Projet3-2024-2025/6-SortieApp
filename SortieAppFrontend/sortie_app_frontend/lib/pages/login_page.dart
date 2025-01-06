import 'package:get/get.dart';
import 'package:flutter/material.dart';
import 'package:sortie_app_frontend/controllers/login_controller.dart';

class loginPage extends StatelessWidget {
  final LoginController controller = Get.put(LoginController());

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF7F9FC), // Couleur de fond gris clair
      appBar: AppBar(
        title: const Text(
          'Login',
          style: TextStyle(
            color: Colors.white, // Titre en blanc
            fontWeight: FontWeight.bold, // Texte en gras
          ),
        ),
        backgroundColor: const Color(0xFF0052CC), // Bleu marine
        centerTitle: true,
        iconTheme: const IconThemeData(color: Colors.white), // Icônes en blanc
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Logo de l'application
            Image.asset(
              'assets/images/SortieApp_logo_no_bg.png',
              height: 120, // Taille du logo
            ),
            const SizedBox(height: 32), // Espacement entre le logo et les champs

            // Champ Email
            TextField(
              onChanged: (value) => controller.email.value = value,
              decoration: InputDecoration(
                labelText: 'Email',
                labelStyle: const TextStyle(color: Color(0xFF0052CC)), // Bleu marine
                filled: true,
                fillColor: Colors.white,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12), // Coins arrondis
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: const BorderSide(color: Color(0xFF0052CC), width: 1.5),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: const BorderSide(color: Color(0xFFFFC107), width: 2),
                ),
              ),
            ),
            const SizedBox(height: 16),

            // Champ Password
            TextField(
              onChanged: (value) => controller.password.value = value,
              obscureText: true,
              decoration: InputDecoration(
                labelText: 'Password',
                labelStyle: const TextStyle(color: Color(0xFF0052CC)), // Bleu marine
                filled: true,
                fillColor: Colors.white,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12), // Coins arrondis
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: const BorderSide(color: Color(0xFF0052CC), width: 1.5),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: const BorderSide(color: Color(0xFFFFC107), width: 2),
                ),
              ),
            ),
            const SizedBox(height: 16),

            // Bouton Login ou Loader
            Obx(() {
              return controller.isLoading.value
                  ? const CircularProgressIndicator() // Loader standard
                  : ElevatedButton(
                onPressed: () {
                  controller.login();
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF0052CC), // Bleu marine
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                ),
                child: const Text(
                  'Login',
                  style: TextStyle(
                    color: Colors.white,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              );
            }),
          ],
        ),
      ),
    );
  }
}
