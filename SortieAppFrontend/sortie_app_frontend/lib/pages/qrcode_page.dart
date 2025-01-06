import 'dart:convert';
import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:sortie_app_frontend/utils/backendRequest.dart';

import '../utils/tokenUtils.dart';

class QRCodePage extends StatefulWidget {
  const QRCodePage({Key? key}) : super(key: key);

  @override
  State<QRCodePage> createState() => _QRCodePageState();
}

class _QRCodePageState extends State<QRCodePage> {
  String userName = '';
  Uint8List? qrCodeBinary;
  bool isLoading = true;
  Map<String, dynamic>? userData;

  @override
  void initState() {
    super.initState();
    fetchUserProfile();
    _loadUserData();
  }

  Future<void> _loadUserData() async {
    final header = await getHeader();
    try {
      final response = await http.get(
        Uri.parse('${getBackendUrl()}/qrcodes/generateFromUser'),
        headers: header,
      );

      if (response.statusCode == 200) {
        setState(() {
          qrCodeBinary = response.bodyBytes; // Récupère l'image binaire
          if (userData != null) {
            userName = '${userData!['name_user']} ${userData!['lastname_user']}';
          } else {
            userName = 'User data is not loaded';
          }
          isLoading = false;
        });
      } else {
        setState(() {
          userName = 'Error loading QR Code: ${response.statusCode}';
          isLoading = false;
        });
      }
    } catch (e) {
      setState(() {
        userName = 'Failed to fetch QR Code\n $e';
        isLoading = false;
      });
    }
  }

  Future<void> fetchUserProfile() async {
    final String url = '${getBackendUrl()}/users/profile';
    final header = await getHeader();

    try {
      final response = await http.get(
        Uri.parse(url),
        headers: header,
      );

      if (response.statusCode == 200) {
        setState(() {
          userData = json.decode(response.body);
          isLoading = false;
        });
      } else {
        setState(() {
          isLoading = false;
        });
        throw Exception('Failed to load profile: ${response.statusCode}');
      }
    } catch (e) {
      setState(() {
        isLoading = false;
      });
      print('Error fetching user profile: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.white),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
        title: const Text(
          'QR Code',
          style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
        ),
        backgroundColor: const Color(0xFF0052CC), // Couleur bleu marine
      ),
      body: Center(
        child: isLoading
            ? const CircularProgressIndicator()
            : Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              // Nom de l'utilisateur
              Text(
                userName,
                textAlign: TextAlign.center,
                style: const TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                  color: Colors.black,
                ),
              ),
              const SizedBox(height: 20),

              // QR Code
              qrCodeBinary != null
                  ? Container(
                padding: const EdgeInsets.all(16.0),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(12),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.grey.withOpacity(0.5),
                      spreadRadius: 2,
                      blurRadius: 5,
                      offset: const Offset(0, 3),
                    ),
                  ],
                ),
                child: Image.memory(
                  qrCodeBinary!,
                  width: 200,
                  height: 200,
                ),
              )
                  : const Text(
                'No QR Code available',
                style: TextStyle(
                  fontSize: 16,
                  color: Colors.red,
                  fontWeight: FontWeight.bold,
                ),
              ),

              const SizedBox(height: 20),

              // Bouton pour recharger le QR Code
              ElevatedButton(
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF0052CC),
                  padding: const EdgeInsets.symmetric(
                      horizontal: 30, vertical: 15),
                ),
                onPressed: _loadUserData,
                child: const Text(
                  'Reload QR Code',
                  style: TextStyle(
                    fontSize: 16,
                    color: Colors.white,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
