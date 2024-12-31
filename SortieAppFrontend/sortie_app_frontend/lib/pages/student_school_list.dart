import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart'; // For kIsWeb
import 'package:http/http.dart' as http;
import 'dart:convert';
import '../utils/router.dart';

class StudentSchoolList extends StatelessWidget {
  const StudentSchoolList({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('User Management'),
        backgroundColor: Color(0xFF87CEEB),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () async {
              redirectHome();
            },
          ),
        ],
      ),
      body: StudentSchoolListScreen(),
    );
  }

}

class StudentSchoolListScreen extends StatefulWidget {
  const StudentSchoolListScreen({super.key});

  @override
  _StudentSchoolListScreenState createState() => _StudentSchoolListScreenState();
}

class _StudentSchoolListScreenState extends State<StudentSchoolListScreen> {
  String getBackendUrl() {
    if (kIsWeb) {
      return 'http://localhost:8081'; // URL Backend for Web
    } else {
      return 'http://10.0.2.2:8081'; // URL Backend for Android Emulator
    }
  }

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    throw UnimplementedError();
  }
  
}