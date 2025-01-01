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
    _loadUserData();
    fetchUserProfile();
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
          userName='${userData!['name_user']} ${userData!['lastname_user']}';
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
    final String? accessToken = await getAccesToken();
    if (accessToken != null) {
      print("Access Token: $accessToken");
    } else {
      print("No Access Token found.");
    }

    try {
      final response = await http.get(
        Uri.parse(url),
        headers: {
          'Authorization': 'Bearer $accessToken',
        },
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
        title: const Text('QR Code'),
        backgroundColor: Color(0xFF87CEEB),
      ),
      body: Center(
        child: isLoading
            ? const CircularProgressIndicator()
            : Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              userName,
              style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 20),
            qrCodeBinary != null
                ? Image.memory(qrCodeBinary!)
                : const Text('No QR Code available'),
          ],
        ),
      ),
    );
  }
}
