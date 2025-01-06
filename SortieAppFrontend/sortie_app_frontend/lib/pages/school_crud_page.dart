import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:get/get.dart';

import '../utils/backendRequest.dart';
import '../utils/router.dart';

class SchoolsCrud extends StatelessWidget {
  const SchoolsCrud({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: const Color(0xFF0052CC), // Bleu marine
        title: const Text(
          'Schools Management',
          style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.arrow_back, color: Colors.white), // Bouton logout
            onPressed: () async {
              redirectHome(); // Redirection à la page d'accueil
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
  late String apiUrl;

  List schools = [];
  List filteredSchools = [];
  final TextEditingController searchController = TextEditingController();

  @override
  void initState() {
    super.initState();
    apiUrl = '${getBackendUrl()}/schools';
    fetchSchools();

    // Écoute les changements dans la barre de recherche
    searchController.addListener(() {
      filterSchools();
    });
  }

  Future<void> fetchSchools() async {
    try {
      var header = await getHeader();
      final response = await http.get(Uri.parse(apiUrl), headers: header);
      if (response.statusCode == 200) {
        setState(() {
          schools = json.decode(response.body);
          filteredSchools = schools; // Initialiser la liste filtrée
        });
      } else {
        throw Exception('Failed to load schools');
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  void filterSchools() {
    String query = searchController.text.toLowerCase();
    setState(() {
      filteredSchools = schools.where((school) {
        return school['name_school'].toLowerCase().contains(query);
      }).toList();
    });
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
        fetchSchools(); // Rafraîchit la liste après la mise à jour
      } else {
        throw Exception('Failed to update school');
      }
    } catch (e) {
      print('Error updating school: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: TextField(
              controller: searchController,
              decoration: InputDecoration(
                hintText: 'Search',
                prefixIcon: const Icon(Icons.search, color: Colors.grey),
                filled: true,
                fillColor: Colors.white,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide.none,
                ),
              ),
            ),
          ),
          Expanded(
            child: ListView.builder(
              itemCount: filteredSchools.length,
              itemBuilder: (context, index) {
                final school = filteredSchools[index];
                return Card(
                  margin:
                  const EdgeInsets.symmetric(vertical: 8, horizontal: 16),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: ListTile(
                    title: Text(
                      school['name_school'],
                      style: const TextStyle(
                          fontWeight: FontWeight.bold, fontSize: 16),
                    ),
                    subtitle: Text(school['address_school']),
                    trailing: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        IconButton(
                          icon: const Icon(Icons.edit, color: Colors.blue),
                          onPressed: () {
                            showEditSchoolDialog(
                              school['id_school'],
                              school['name_school'],
                              school['address_school'],
                            );
                          },
                        ),
                        IconButton(
                          icon: const Icon(Icons.delete, color: Colors.red),
                          onPressed: () {
                            showDeleteConfirmationDialog(school['id_school']);
                          },
                        ),
                      ],
                    ),
                  ),
                );
              },
            ),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        backgroundColor: const Color(0xFF0052CC), // Bleu marine
        child: const Icon(Icons.add, color: Colors.white),
        onPressed: showAddSchoolDialog,
      ),
    );
  }

  void showAddSchoolDialog() {
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
              const SizedBox(height: 8),
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
                if (nameController.text.isEmpty ||
                    addressController.text.isEmpty) {
                  Get.snackbar(
                    'Validation Error',
                    'All fields must be filled.',
                    snackPosition: SnackPosition.BOTTOM,
                    backgroundColor: Colors.redAccent,
                    colorText: Colors.white,
                  );
                  return;
                }
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

  void showEditSchoolDialog(
      int idSchool, String currentName, String currentAddress) {
    final TextEditingController nameController =
    TextEditingController(text: currentName);
    final TextEditingController addressController =
    TextEditingController(text: currentAddress);

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
              const SizedBox(height: 8),
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
                if (nameController.text.isEmpty ||
                    addressController.text.isEmpty) {
                  Get.snackbar(
                    'Validation Error',
                    'All fields must be filled.',
                    snackPosition: SnackPosition.BOTTOM,
                    backgroundColor: Colors.redAccent,
                    colorText: Colors.white,
                  );
                  return;
                }
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

  void showDeleteConfirmationDialog(int idSchool) {
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Confirm Deletion'),
          content: const Text('Are you sure you want to delete this school?'),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('No'),
            ),
            TextButton(
              onPressed: () {
                deleteSchool(idSchool);
                Navigator.pop(context);
              },
              child: const Text('Yes'),
            ),
          ],
        );
      },
    );
  }

  Future<void> deleteSchool(int idSchool) async {
    try {
      final response = await http.delete(Uri.parse('$apiUrl/$idSchool'));
      if (response.statusCode == 200) {
        fetchSchools();
        Get.snackbar('Success', 'School deleted successfully');
      } else {
        Get.snackbar('Error', 'Failed to delete school');
      }
    } catch (e) {
      print('Error: $e');
      Get.snackbar('Error', 'An error occurred while deleting');
    }
  }
}
