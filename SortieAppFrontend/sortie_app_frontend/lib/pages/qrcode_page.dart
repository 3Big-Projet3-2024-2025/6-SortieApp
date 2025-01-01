import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;
import 'package:sortie_app_frontend/utils/backendRequest.dart';

class QRCodePage extends StatefulWidget {
  const QRCodePage({Key? key}) : super(key: key);

  @override
  State<QRCodePage> createState() => _QRCodePageState();
}

class _QRCodePageState extends State<QRCodePage> {
  final secureStorage = const FlutterSecureStorage();
  String userName = '';
  String qrCodeUrl = '';
  bool isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadUserData();
  }

  Future<void> _loadUserData() async {
    // Récupérer le userId et token depuis le stockage sécurisé
    final header= await getHeader();
    if ( header != null) {
      try {
        // Requête vers le backend pour récupérer le QR code
        final response = await http.get(
          Uri.parse('http://localhost:8081/qrcodes/generateFromUser'),
          headers: header,
        );

        if (response.statusCode == 200) {
          setState(() {
            qrCodeUrl = response.request?.url.toString() ?? '';
            userName = 'John Doe'; // Remplace avec les vraies données du user
            isLoading = false;
          });
        } else {
          setState(() {
            userName = 'Error loading QR Code';
            isLoading = false;
          });
        }
      } catch (e) {
        setState(() {
          userName = 'Failed to fetch QR Code\n $e';
          isLoading = false;
        });
      }
    } else {
      setState(() {
        userName = 'User not found';
        isLoading = false;
      });
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
            qrCodeUrl.isNotEmpty
                ? Image.network(qrCodeUrl)  // Affiche le QR code récupéré
                : const Text('No QR Code available'),
          ],
        ),
      ),
    );
  }
}
