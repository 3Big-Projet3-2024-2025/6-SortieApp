import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart'; // For kIsWeb
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:file_picker/file_picker.dart';

import '../utils/backendRequest.dart';
import '../utils/router.dart';


void main() {
  runApp(const UserApp());
}

class UserApp extends StatelessWidget {
  const UserApp({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
            'Users Management',
            style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
        backgroundColor: const Color(0xFF0052CC), // Bleu marine
        actions: [
          IconButton(
            icon: const Icon(Icons.arrow_back, color: Colors.white),
            onPressed: () async {
              redirectHome();
            },
          ),
        ],
      ),
      body: const UserListScreen(),
    );
  }
}

class UserListScreen extends StatefulWidget {
  const UserListScreen({super.key});

  @override
  _UserListScreenState createState() => _UserListScreenState();
}

class _UserListScreenState extends State<UserListScreen> {

  late String apiUrl;
  late String rolesApiUrl;
  bool showAllUsers = false;

  List users = [];
  List roles = [];
  List schools = [];

  // For search tab
  List filteredUsers = [];
  String searchQuery = "";

  @override
  void initState() {
    super.initState();
    apiUrl = '${getBackendUrl()}/users';
    rolesApiUrl = '${getBackendUrl()}/roles';
    fetchUsers();
    fetchRoles();
    fetchSchools();
  }

  Future<void> fetchUsers() async {
    try {
      var header = await getHeader();
      final response = await http.get(Uri.parse(showAllUsers ? '${getBackendUrl()}/users/getAllUsers' : apiUrl),headers: header);
      if (response.statusCode == 200) {
        setState(() {
          users = json.decode(response.body);
          filteredUsers = users;
        });
      } else {
        throw Exception('Failed to load users');
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  void filterUsers(String query) {
    setState(() {
      searchQuery = query.toLowerCase();
      filteredUsers = users.where((user) {
        final name = user['name_user']?.toLowerCase() ?? '';
        final lastname = user['lastname_user']?.toLowerCase() ?? '';
        final email = user['email']?.toLowerCase() ?? '';
        return name.contains(searchQuery) || lastname.contains(searchQuery) || email.contains(searchQuery);
      }).toList();
    });
  }

  Future<void> fetchRoles() async {
    try {
      final header = await getHeader();
      final response = await http.get(Uri.parse(rolesApiUrl), headers: header);
      if (response.statusCode == 200) {
        setState(() {
          roles = json.decode(response.body);
        });
      } else {
        throw Exception('Failed to load roles');
      }
    } catch (e) {
      print('Error fetching roles: $e');
    }
  }

  Future<void> fetchSchools() async {
    try {
      final header = await getHeader();
      final response = await http.get(Uri.parse('${getBackendUrl()}/schools'), headers: header);
      if (response.statusCode == 200) {
        setState(() {
          schools = json.decode(response.body);
        });
      } else {
        throw Exception('Failed to load schools');
      }
    } catch (e) {
      print('Error fetching schools: $e');
    }
  }

  // Add a new user to the backend
  Future<void> addUser(String name_user, String lastname_user, String email, String address_user, int id_role, int id_school) async {
    try {
      final header = await getHeader();
      final response = await http.post(
        Uri.parse(apiUrl),
        headers: header,
        body: json.encode({
          'name_user': name_user,
          'lastname_user': lastname_user,
          'email': email,
          'address_user': address_user,
          'role_user': {'id_role': id_role},
          'school_user': {'id_school': id_school},
        }),
      );
      if (response.statusCode == 200) {
        fetchUsers();
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  // Import CSV Method
  void showImportCSVDialog(BuildContext context) async {
    FilePickerResult? result = await FilePicker.platform.pickFiles(
      type: FileType.custom,
      allowedExtensions: ['csv'],
    );

    if (result != null) {
      Uint8List? fileBytes = result.files.single.bytes;
      String fileName = result.files.single.name;

      try {
        final header = await getHeader();
        var request = http.MultipartRequest(
          'POST',
          Uri.parse('${getBackendUrl()}/users/import'),
        );

        request.headers.addAll(header);

        if (fileBytes != null) {
          request.files.add(
            http.MultipartFile.fromBytes(
              'file',
              fileBytes,
              filename: fileName,
            ),
          );
        }

        var response = await request.send();

        if (response.statusCode == 200) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('CSV imported successfully!'),
              backgroundColor: Colors.green,
            ),
          );
          fetchUsers();
        } else {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('Failed to import CSV.'),
              backgroundColor: Colors.red,
            ),
          );
        }
      } catch (e) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Error: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('No file selected.'),
          backgroundColor: Colors.orange,
        ),
      );
    }
  }

  Future<void> updateUser(
      int id,
      String name_user,
      String lastname_user,
      String email,
      String address_user,
      int id_role,
      bool activated,
      int id_school) async {
    try {
      final header = await getHeader();
      final response = await http.put(
        Uri.parse('$apiUrl/$id'),
        headers: header,
        body: json.encode({
          'name_user': name_user,
          'lastname_user': lastname_user,
          'email': email,
          'address_user': address_user,
          'role_user': {'id_role': id_role},
          'school_user': {'id_school': id_school},
          'activated': activated
        }),
      );
      if (response.statusCode == 200) {
        fetchUsers();
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  Future<void> deleteUser(int id) async {
    final confirm = await showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Confirm Deletion'),
        content: const Text('Are you sure you want to delete this user?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('Yes'),
          ),
        ],
      ),
    );

    if (confirm == true) {
      try {
        final header = await getHeader();
        final response = await http.delete(Uri.parse('$apiUrl/$id'), headers: header);
        if (response.statusCode == 200) {
          fetchUsers();
        }
      } catch (e) {
        print('Error: $e');
      }
    }
  }

  void toggleUserView() {
    setState(() {
      showAllUsers = !showAllUsers;
    });
    fetchUsers();
  }

  bool isValidEmail(String email) {
    final emailRegex = RegExp(r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$');
    return emailRegex.hasMatch(email);
  }

  // Show a dialog to add a new user
  void showAddUserDialog() async {
    final TextEditingController nameController = TextEditingController();
    final TextEditingController lastnameController = TextEditingController();
    final TextEditingController emailController = TextEditingController();
    final TextEditingController addressController = TextEditingController();

    int? selectedRoleId;
    int? selectedSchoolId;
    String? errorMessage;

    showDialog(
      context: context,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setState) {
            return AlertDialog(
              title: const Text('Add User'),
              content: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  if (errorMessage != null)
                    Padding(
                      padding: const EdgeInsets.only(bottom: 8.0),
                      child: Text(
                        errorMessage!,
                        style: const TextStyle(color: Colors.red),
                      ),
                    ),
                  TextField(
                    controller: nameController,
                    decoration: const InputDecoration(hintText: 'Enter name'),
                  ),
                  TextField(
                    controller: lastnameController,
                    decoration: const InputDecoration(hintText: 'Enter lastname'),
                  ),
                  TextField(
                    controller: emailController,
                    decoration: const InputDecoration(hintText: 'Enter email'),
                  ),
                  TextField(
                    controller: addressController,
                    decoration: const InputDecoration(hintText: 'Enter address'),
                  ),
                  DropdownButtonFormField<int>(
                    decoration: const InputDecoration(hintText: 'Select Role'),
                    items: roles.map<DropdownMenuItem<int>>((role) {
                      return DropdownMenuItem<int>(
                        value: role['id_role'],
                        child: Text(role['name_role']),
                      );
                    }).toList(),
                    onChanged: (value) {
                      selectedRoleId = value;
                    },
                  ),
                  DropdownButtonFormField<int>(
                    decoration: const InputDecoration(hintText: 'Select School'),
                    items: schools.isNotEmpty
                        ? schools.map<DropdownMenuItem<int>>((school) {
                      return DropdownMenuItem<int>(
                        value: school['id_school'],
                        child: Text(school['name_school']),
                      );
                    }).toList()
                        : [],
                    onChanged: (value) {
                      selectedSchoolId = value;
                    },
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
                        lastnameController.text.isEmpty ||
                        emailController.text.isEmpty ||
                        addressController.text.isEmpty ||
                        selectedRoleId == null) {
                      setState(() {
                        errorMessage = 'All fields must be filled out.';
                      });
                    } else if (!isValidEmail(emailController.text)) {
                      setState(() {
                        errorMessage = 'Please enter a valid email address.';
                      });
                    } else {
                      setState(() {
                        errorMessage = null;
                      });
                      addUser(
                        nameController.text,
                        lastnameController.text,
                        emailController.text,
                        addressController.text,
                        selectedRoleId!,
                        selectedSchoolId!,
                      );
                      Navigator.pop(context);
                    }
                  },
                  child: const Text('Add'),
                ),
              ],
            );
          },
        );
      },
    );
  }

  void showEditUserDialog(
      int id,
      String currentName,
      String currentLastname,
      String currentEmail,
      String currentAddress,
      int currentRoleId,
      bool currentActivated,
      int? selectedSchoolId, // step1
      ) async {
    final TextEditingController nameController = TextEditingController();
    final TextEditingController lastnameController = TextEditingController();
    final TextEditingController emailController = TextEditingController();
    final TextEditingController addressController = TextEditingController();

    nameController.text = currentName;
    lastnameController.text = currentLastname;
    emailController.text = currentEmail;
    addressController.text = currentAddress;

    int? selectedRoleId = currentRoleId;
    String? errorMessage;

    showDialog(
      context: context,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setState) {
            return AlertDialog(
              title: const Text('Edit User'),
              content: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  if (errorMessage != null)
                    Padding(
                      padding: const EdgeInsets.only(bottom: 8.0),
                      child: Text(
                        errorMessage!,
                        style: const TextStyle(color: Colors.red),
                      ),
                    ),
                  TextField(
                    controller: nameController,
                    decoration: const InputDecoration(hintText: 'Enter name'),
                  ),
                  TextField(
                    controller: lastnameController,
                    decoration: const InputDecoration(hintText: 'Enter lastname'),
                  ),
                  TextField(
                    controller: emailController,
                    decoration: const InputDecoration(hintText: 'Enter email'),
                  ),
                  TextField(
                    controller: addressController,
                    decoration: const InputDecoration(hintText: 'Enter address'),
                  ),
                  DropdownButtonFormField<int>(
                    value: selectedRoleId,
                    decoration: const InputDecoration(hintText: 'Select Role'),
                    items: roles.map<DropdownMenuItem<int>>((role) {
                      return DropdownMenuItem<int>(
                        value: role['id_role'],
                        child: Text(role['name_role']),
                      );
                    }).toList(),
                    onChanged: (value) {
                      setState(() {
                        selectedRoleId = value;
                      });
                    },
                  ),
                  DropdownButtonFormField<int>(
                    value: selectedSchoolId,
                    decoration: const InputDecoration(hintText: 'Select School'),
                    items: schools.map<DropdownMenuItem<int>>((school) {
                      return DropdownMenuItem<int>(
                        value: school['id_school'],
                        child: Text(school['name_school']),
                      );
                    }).toList(),
                    onChanged: (value) {
                      setState(() {
                        selectedSchoolId = value;
                      });
                    },
                  ),//step2
                  TextField(
                    enabled: false,
                    decoration: InputDecoration(
                      labelText: 'Activated',
                      hintText: currentActivated ? 'Yes' : 'No',
                      labelStyle: const TextStyle(color: Colors.grey),
                      border: const OutlineInputBorder(),
                    ),
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
                        lastnameController.text.isEmpty ||
                        emailController.text.isEmpty ||
                        addressController.text.isEmpty ||
                        selectedRoleId == null) {
                      setState(() {
                        errorMessage = 'All fields must be filled out.';
                      });
                    } else {
                      setState(() {
                        errorMessage = null;
                      });
                      updateUser(
                        id,
                        nameController.text,
                        lastnameController.text,
                        emailController.text,
                        addressController.text,
                        selectedRoleId!,
                        currentActivated,
                        selectedSchoolId!,
                      );
                      Navigator.pop(context);
                    }
                  },
                  child: const Text('Update'),
                ),
              ],
            );
          },
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF7F9FC), // Gris clair pour le fond
      body: Column(
        children: [
          // Barre de recherche en dehors de l'AppBar
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
                prefixIcon: const Icon(Icons.search, color: Colors.grey),
                filled: true,
                fillColor: Colors.white,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(8.0),
                  borderSide: BorderSide.none,
                ),
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
                final address = user['address_user'] ?? 'No Address';
                final email = user['email'] ?? 'No Email';
                final isActive = user['activated'] == true;
                final schoolName = user['school_user']?['name_school'] ?? 'No School';

                final String? base64Image = user['picture_user'];
                Uint8List? imageBytes = (base64Image != null && base64Image.trim().isNotEmpty)
                    ? base64Decode(base64Image)
                    : null;

                return ListTile(
                  leading: GestureDetector(
                    onTap: () {
                      showDialog(
                        context: context,
                        builder: (BuildContext context) {
                          return Dialog(
                            child: Column(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                imageBytes != null
                                    ? Image.memory(
                                  imageBytes,
                                  fit: BoxFit.cover,
                                )
                                    : Image.asset(
                                  'assets/images/default_profile.jpg',
                                  fit: BoxFit.cover,
                                ),
                                TextButton(
                                  onPressed: () {
                                    Navigator.of(context).pop();
                                  },
                                  child: const Text('Close'),
                                ),
                              ],
                            ),
                          );
                        },
                      );
                    },
                    child: imageBytes != null
                        ? Image.memory(
                      imageBytes,
                      width: 50,
                      height: 50,
                      fit: BoxFit.cover,
                    )
                        : Image.asset(
                      'assets/images/default_profile.jpg',
                      width: 50,
                      height: 50,
                      fit: BoxFit.cover,
                    ),
                  ),
                  title: Text('${user['name_user']} ${user['lastname_user']}'),
                  subtitle: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('$roleName\n$schoolName\n$email\n$address'),
                      Text(
                        'Active: ${isActive ? 'Yes' : 'No'}',
                        style: TextStyle(
                          color: isActive ? Colors.green : Colors.red,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
                  trailing: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      IconButton(
                        icon: const Icon(Icons.edit, color: Colors.blue),
                        onPressed: () => showEditUserDialog(
                          user['id'],
                          user['name_user'] ?? '',
                          user['lastname_user'] ?? '',
                          user['email'] ?? '',
                          user['address_user'] ?? '',
                          user['role_user']?['id_role'] ?? 0,
                          user['activated'] ?? false,
                          user['school_user']['id_school'] ?? 0,
                        ),
                      ),
                      IconButton(
                        icon: const Icon(Icons.delete, color: Colors.red),
                        onPressed: () => deleteUser(user['id']),
                      ),
                    ],
                  ),
                );
              },
            ),
          ),
        ],
      ),
      floatingActionButton: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          FloatingActionButton(
            backgroundColor: Colors.orange,
            onPressed: () => showImportCSVDialog(context),
            child: const Icon(Icons.upload_file, color: Colors.white),
          ),
          const SizedBox(width: 15),
          FloatingActionButton(
            backgroundColor: Colors.green,
            onPressed: showAddUserDialog,
            child: const Icon(Icons.add, color: Colors.white),
          ),
          const SizedBox(width: 15),
          FloatingActionButton(
            backgroundColor: Colors.blue,
            onPressed: toggleUserView,
            child: Text(
              showAllUsers ? 'Active' : 'All',
              textAlign: TextAlign.center,
              style: const TextStyle(color: Colors.white, fontSize: 10),
            ),
          ),
        ],
      ),
    );
  }
}