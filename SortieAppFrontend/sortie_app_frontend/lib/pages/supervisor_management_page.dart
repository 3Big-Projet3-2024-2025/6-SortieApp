import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import '../utils/backendRequest.dart';
import '../utils/router.dart';

class Supervisors_management_page extends StatelessWidget {
  const Supervisors_management_page({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Sortie\'App'),
        backgroundColor: Color(0xFF87CEEB),
      ),
      body: const SupervisorsManagementScreen(),
    );
  }
}

class SupervisorsManagementScreen extends StatefulWidget {
  const SupervisorsManagementScreen({super.key});

  @override
  _SupervisorsManagementScreenState createState() =>
      _SupervisorsManagementScreenState();
}

class _SupervisorsManagementScreenState
    extends State<SupervisorsManagementScreen> {
  List users = [];
  List filteredUsers = [];
  String searchQuery = "";

  @override
  void initState() {
    super.initState();
    fetchUsers();
  }

  Future<void> fetchUsers() async {
    try {
      var header = await getHeader();
      var uri = getBackendUrl();
      final profileResponse = await http.get(
        Uri.parse('${uri}/users/profile'),
        headers: header,
      );

      if (profileResponse.statusCode == 200) {
        final userData = json.decode(profileResponse.body);
        final schoolId = userData['school_user']['id_school'];

        final SupervisorsResponse = await http.get(
          Uri.parse('${uri}/schools/getSupervisorBySchool/$schoolId'),
          headers: header,
        );

        if (SupervisorsResponse.statusCode == 200) {
          setState(() {
            users = json.decode(SupervisorsResponse.body);
            filteredUsers = users;
          });
        } else {
          throw Exception('Failed to load supervisors');
        }
      } else {
        throw Exception('Failed to load user profile');
      }
    } catch (e) {
      print('Error fetching Supervisors: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // Barre de recherche en dessous de l'AppBar
        Padding(
          padding: const EdgeInsets.all(8.0),
          child: TextField(
            onChanged: (query) {
              setState(() {
                searchQuery = query.toLowerCase();
                filteredUsers = users.where((user) {
                  final name = user['name_user']?.toLowerCase() ?? '';
                  final lastname = user['lastname_user']?.toLowerCase() ?? '';
                  final email = user['email']?.toLowerCase() ?? '';
                  return name.contains(searchQuery) ||
                      lastname.contains(searchQuery) ||
                      email.contains(searchQuery);
                }).toList();
              });
            },
            decoration: InputDecoration(
              hintText: 'Search',
              prefixIcon: const Icon(Icons.search),
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(8.0),
                borderSide: BorderSide.none,
              ),
              filled: true,
              fillColor: Colors.white,
              contentPadding: const EdgeInsets.symmetric(vertical: 12.0),
            ),
          ),
        ),
        // Liste des utilisateurs
        Expanded(
          child: ListView.builder(
            itemCount: filteredUsers.length,
            itemBuilder: (context, index) {
              final user = filteredUsers[index];
              final roleName = user['role_user']?['name_role'] ?? 'Unknown Role';

              return ListTile(
                title: Text('${user['name_user']} ${user['lastname_user']}'),
                subtitle: Text('Role: $roleName'),
              );
            },
          ),
        ),
      ],
    );
  }
}
