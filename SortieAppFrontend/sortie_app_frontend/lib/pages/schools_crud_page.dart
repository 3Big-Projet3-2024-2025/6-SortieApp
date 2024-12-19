import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart'; // For kIsWeb
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:get/get.dart';

import '../utils/router.dart';

class SchoolsCrud extends StatelessWidget {
  const SchoolsCrud({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Schools Management'),
        backgroundColor: const Color(0xFF87CEEB),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () async {
              redirectHome();
            },
          ),
        ],
      ),
      body: const SchoolListScreen(),
    );
  }
}

class SchoolListScreen extends StatefulWidget {
  const SchoolListScreen({super.key});

  @override
  _SchoolListScreenState createState() => _SchoolListScreenState();
}

class _SchoolListScreenState extends State<SchoolListScreen> {
  String getBackendUrl() {
    if (kIsWeb) {
      return 'http://localhost:8081'; // URL Backend for Web
    } else {
      return 'http://10.0.2.2:8081'; // URL Backend for Android Emulator
    }
  }

  late String apiUrl;

  List schools = [];

  @override
  void initState() {
    super.initState();
    apiUrl = '${getBackendUrl()}/schools';
    fetchSchools();
  }

  Future<void> fetchSchools() async {
    try {
      final response = await http.get(Uri.parse(apiUrl));
      if (response.statusCode == 200) {
        setState(() {
          schools = json.decode(response.body);
        });
      } else {
        throw Exception('Failed to load schools');
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  Future<void> addSchool(String nameSchool, String addressSchool) async {
    try {
      final response = await http.post(
        Uri.parse(apiUrl),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'name_school': nameSchool,
          'address_school': addressSchool,
        }),
      );
      if (response.statusCode == 200) {
        fetchSchools();
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  Future<void> updateSchool(
      int idSchool, String nameSchool, String addressSchool) async {
    try {
      final response = await http.put(
        Uri.parse('$apiUrl/$idSchool'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'name_school': nameSchool,
          'address_school': addressSchool,
        }),
      );
      if (response.statusCode == 200) {
        fetchSchools();
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  Future<void> deleteSchool(int idSchool) async {
    try {
      final response = await http.delete(Uri.parse('$apiUrl/$idSchool'));
      if (response.statusCode == 200) {
        fetchSchools();
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  void showAddSchoolDialog() async {
    final TextEditingController nameController = TextEditingController();
    final TextEditingController addressController = TextEditingController();

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Add School'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: nameController,
                decoration: const InputDecoration(hintText: 'Enter school name'),
              ),
              TextField(
                controller: addressController,
                decoration:
                const InputDecoration(hintText: 'Enter school address'),
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Cancel'),
            ),
            TextButton(
              onPressed: () {
                addSchool(nameController.text, addressController.text);
                Navigator.pop(context);
              },
              child: const Text('Add'),
            ),
          ],
        );
      },
    );
  }

  void showEditSchoolDialog(
      int idSchool, String currentName, String currentAddress) async {
    final TextEditingController nameController = TextEditingController();
    final TextEditingController addressController = TextEditingController();

    nameController.text = currentName;
    addressController.text = currentAddress;

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Edit School'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: nameController,
                decoration:
                const InputDecoration(hintText: 'Enter school name'),
              ),
              TextField(
                controller: addressController,
                decoration:
                const InputDecoration(hintText: 'Enter school address'),
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Cancel'),
            ),
            TextButton(
              onPressed: () {
                updateSchool(
                  idSchool,
                  nameController.text,
                  addressController.text,
                );
                Navigator.pop(context);
              },
              child: const Text('Update'),
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: ListView.builder(
        itemCount: schools.length,
        itemBuilder: (context, index) {
          final school = schools[index];

          return ListTile(
            title: Text(school['name_school']),
            subtitle: Text(school['address_school']),
            trailing: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                IconButton(
                  icon: const Icon(Icons.edit),
                  onPressed: () => showEditSchoolDialog(
                    school['id_school'],
                    school['name_school'],
                    school['address_school'],
                  ),
                ),
                IconButton(
                  icon: const Icon(Icons.delete),
                  onPressed: () => deleteSchool(school['id_school']),
                ),
              ],
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: showAddSchoolDialog,
        child: const Icon(Icons.add),
      ),
    );
  }
}
