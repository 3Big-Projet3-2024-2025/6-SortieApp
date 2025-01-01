import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:sortie_app_frontend/pages/student_details_with_autorisations.dart';

import '../utils/router.dart';
import '../utils/tokenUtils.dart';
import '../utils/backendRequest.dart';

class StudentListPage extends StatelessWidget {
  const StudentListPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Sortie\'App'),
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
      body: const StudentListScreen(),
    );
  }
}

class StudentListScreen extends StatefulWidget {
  const StudentListScreen({Key? key}) : super(key: key);

  @override
  State<StudentListScreen> createState() => _StudentListScreen();
}

class _StudentListScreen extends State<StudentListScreen> {
  List students = [];
  bool isLoading = true;

  @override
  void initState() {
    super.initState();
    fetchSchoolAndStudents();
  }

  Future<void> fetchSchoolAndStudents() async {
    try {
      final String? accessToken = await getAccesToken();
      if (accessToken == null) {
        throw Exception('No access token found');
      }
      var header = await getHeader();
      var uri = getBackendUrl();
      // Récupérer les données du profil de l'utilisateur pour obtenir l'ID de l'école
      final profileResponse = await http.get(
        Uri.parse('${uri}/users/profile'),
        headers: header,
      );

      if (profileResponse.statusCode == 200) {
        final userData = json.decode(profileResponse.body);
        final schoolId = userData['school_user']['id_school'];

        // Récupérer la liste des étudiants de la même école
        final studentsResponse = await http.get(
          Uri.parse('${uri}/schools/getStudentsBySchool/$schoolId'),
          headers: {
            'Authorization': 'Bearer $accessToken',
          },
        );

        if (studentsResponse.statusCode == 200) {
          setState(() {
            students = json.decode(studentsResponse.body);
            isLoading = false;
          });
        } else {
          throw Exception('Failed to load students');
        }
      } else {
        throw Exception('Failed to load user profile');
      }
    } catch (e) {
      setState(() {
        isLoading = false;
      });
      print('Error fetching students: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Students List'),
      ),
      body: isLoading
          ? const Center(child: CircularProgressIndicator())
          : ListView.builder(
        itemCount: students.length,
        itemBuilder: (context, index) {
          final student = students[index];
          return Card(
            child: ListTile(
              title: Text('${student['name_user']} ${student['lastname_user']}'),
              subtitle: Text('${student['email']}'),
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => StudentDetailPage(studentId: student['id']),
                  ),
                );
              },
              leading: CircleAvatar(
                backgroundImage: NetworkImage('data:image/png;base64,${student['picture_user']}'),
              ),
            ),
          );
        },
      ),
    );
  }
}
