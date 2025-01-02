import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import '../utils/backendRequest.dart';

class QRResultPage extends StatefulWidget {
  final String userId;

  const QRResultPage({Key? key, required this.userId}) : super(key: key);

  @override
  State<QRResultPage> createState() => _QRResultPageState();
}

class _QRResultPageState extends State<QRResultPage> {
  Map<String, dynamic>? userAutorisationData; // Pour stocker les données
  bool isLoading = true; // Indique si les données sont en cours de chargement

  @override
  void initState() {
    super.initState();
    fetchUserAutorisation();
  }

  Future<void> fetchUserAutorisation() async {
    try {
      var header = await getHeader();
      var uri = getBackendUrl();
      final response = await http.get(Uri.parse('$uri/qrcodes/3'),headers: header,);

      if (response.statusCode == 200) {
        setState(() {
          userAutorisationData = json.decode(response.body);
          isLoading = false;
        });
      } else {
        setState(() {
          isLoading = false;
        });
        throw Exception('Failed to load user authorization: ${response.statusCode}');
      }
    } catch (e) {
      print(e.toString());
      setState(() {
        isLoading = false;
      });
      print('Error fetching user authorization: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    if (isLoading) {
      return Scaffold(
        appBar: AppBar(
          title: const Text('QR Code Result'),
        ),
        body: const Center(
          child: CircularProgressIndicator(),
        ),
      );
    }

    if (userAutorisationData == null) {
      return Scaffold(
        appBar: AppBar(
          title: const Text('QR Code Result'),
        ),
        body: const Center(
          child: Text('Failed to fetch user data.'),
        ),
      );
    }

    // Extraire les données de l'objet renvoyé
    final user = userAutorisationData!['user'];
    final canGo = userAutorisationData!['canGo'];
    final profilePicture = user['picture_user'];

    return Scaffold(
      appBar: AppBar(
        title: const Text('QR Code Result'),
        backgroundColor: Colors.blue,
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            if (profilePicture != null && profilePicture.isNotEmpty)
              CircleAvatar(
                radius: 50,
                backgroundImage: MemoryImage(base64Decode(profilePicture)),
              )
            else
              const CircleAvatar(
                radius: 50,
                child: Icon(Icons.person, size: 50),
              ),
            const SizedBox(height: 20),
            Text(
              '${user['name_user']} ${user['lastname_user']}',
              style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 10),
            Text(
              canGo ? 'Can go' : 'Can not go',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: canGo ? Colors.green : Colors.red,
              ),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () {
                Navigator.pop(context);
              },
              child: const Text('Back to Scanner'),
            ),
          ],
        ),
      ),
    );
  }
}

