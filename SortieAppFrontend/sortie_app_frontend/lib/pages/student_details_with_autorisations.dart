import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import '../utils/backendRequest.dart';
import 'AuthorizationManagementPage.dart';
import 'my_profile_page.dart';

class StudentDetailPage extends StatefulWidget {
  final int studentId;

  const StudentDetailPage({Key? key, required this.studentId}) : super(key: key);

  @override
  State<StudentDetailPage> createState() => _StudentDetailPageState();
}

class _StudentDetailPageState extends State<StudentDetailPage> {
  Map<String, dynamic>? studentDetails;
  bool isLoading = true;
  late String uri;
  List autorisations = [];
  List ShowedAutorisations = [];
  @override
  void initState() {
    super.initState();
    uri = getBackendUrl();
    fetchStudentDetails();
    fetchAutorisations();
  }

  Future<void> fetchStudentDetails() async {
    try {
      var header = await getHeader();
      var uri = getBackendUrl();

      // Récupérer les détails de l'étudiant
      final response = await http.get(
        Uri.parse('$uri/users/${widget.studentId}'),
        headers: header,
      );

      if (response.statusCode == 200) {
        setState(() {
          studentDetails = json.decode(response.body);
          isLoading = false;
        });
      } else {
        throw Exception('Failed to load student details${response.statusCode}');
      }
    } catch (e) {
      setState(() {
        isLoading = false;
      });
      print('Error fetching student details: $e');
    }
  }


  Future<void> fetchAutorisations({int page = 0}) async {
    try {
      var header = await getHeader();
      final response = await http.get(
        Uri.parse('${uri}/Autorisations'),
        headers: header,
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        setState(() {
          autorisations = data;
          ShowedAutorisations = List.from(data);
        });
      } else {
        throw Exception('Failed to load autorisations');
      }
    } catch (e) {
      print('Error fetching autorisations: $e');
    }
  }
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Student Details'),
      ),
      body: isLoading
          ? const Center(child: CircularProgressIndicator())
          : studentDetails == null
          ? const Center(child: Text('Failed to load student data.'))
          : Padding(
        padding: const EdgeInsets.all(16.0),
        child: ListView(
          children: [
            // Afficher la photo de profil de l'étudiant
            Center(
              child: CircleAvatar(
                radius: 80,
                backgroundImage: studentDetails!['picture_user'] == null
                    ? const AssetImage('assets/default_profile_pic.png')
                    : MemoryImage(base64Decode(studentDetails!['picture_user'])),
              ),
            ),
            const SizedBox(height: 20),
            ProfileField(
              label: 'Name',
              value: '${studentDetails!['name_user']} ${studentDetails!['lastname_user']}',
            ),
            const SizedBox(height: 10),
            ProfileField(
              label: 'Email',
              value: studentDetails!['email'],
            ),
            const SizedBox(height: 10),
            ProfileField(
              label: 'Address',
              value: studentDetails!['address_user'] ?? 'No address available',
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => AuthorizationManagementPage(studentId: widget.studentId),
                  ),
                );
              },
              child: const Text('Manage Authorizations'),
            ),
          ],
        ),
      ),
    );
  }
}