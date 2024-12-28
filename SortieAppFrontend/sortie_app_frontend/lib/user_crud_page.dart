import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart'; // For kIsWeb
import 'package:http/http.dart' as http;
import 'dart:convert';

void main() {
  runApp(const UserApp());
}

class UserApp extends StatelessWidget {
  const UserApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const UserListScreen(),
    );
  }
}

class UserListScreen extends StatefulWidget {
  const UserListScreen({super.key});

  @override
  _UserListScreenState createState() => _UserListScreenState();
}

class _UserListScreenState extends State<UserListScreen> {
  String getBackendUrl() {
    if (kIsWeb) {
      return 'http://localhost:8081'; // URL Backend for Web
    } else {
      return 'http://10.0.2.2:8081'; // URL Backend for Android Emulator
    }
  }

  late String apiUrl;
  late String rolesApiUrl;
  bool showAllUsers = false;

  List users = [];
  List roles = [];

  @override
  void initState() {
    super.initState();
    apiUrl = '${getBackendUrl()}/users';
    rolesApiUrl = '${getBackendUrl()}/roles';
    fetchUsers();
    fetchRoles();
  }

  Future<void> fetchUsers() async {
    try {
      final response = await http.get(Uri.parse(showAllUsers ? '${getBackendUrl()}/users/getAllUsers' : apiUrl));
      if (response.statusCode == 200) {
        setState(() {
          users = json.decode(response.body);
        });
      } else {
        throw Exception('Failed to load users');
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  Future<void> fetchRoles() async {
    try {
      final response = await http.get(Uri.parse(rolesApiUrl));
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

  Future<void> addUser(String name_user, String lastname_user,
      String email, String address_user, int id_role) async {
    try {
      final response = await http.post(
        Uri.parse(apiUrl),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'name_user': name_user,
          'lastname_user': lastname_user,
          'email': email,
          'address_user': address_user,
          'role_user': {'id_role': id_role},
        }),
      );
      if (response.statusCode == 200) {
        fetchUsers();
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  Future<void> updateUser(int id, String name_user, String lastname_user,
      String email, String address_user, int id_role, bool activated) async {
    try {
      final response = await http.put(
        Uri.parse('$apiUrl/$id'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'name_user': name_user,
          'lastname_user': lastname_user,
          'email': email,
          'address_user': address_user,
          'role_user': {'id_role': id_role},
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
        final response = await http.delete(Uri.parse('$apiUrl/$id'));
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

  void showAddUserDialog() async {
    final TextEditingController nameController = TextEditingController();
    final TextEditingController lastnameController = TextEditingController();
    final TextEditingController emailController = TextEditingController();
    final TextEditingController addressController = TextEditingController();

    int? selectedRoleId;
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
      appBar: AppBar(
        title: const Text('User Management'),
      ),
      body: ListView.builder(
        itemCount: users.length,
        itemBuilder: (context, index) {
          final user = users[index];

          final roleName = user['role_user']?['name_role'] ?? 'Unknown Role';
          final address = user['address_user'] ?? 'No Address';
          final email = user['email'] ?? 'No Email';
          final isActive = user['activated'] == true;

          final String? base64Image = user['picture_user'];
          Uint8List? imageBytes = (base64Image != null && base64Image.trim().isNotEmpty)
              ? base64Decode(base64Image)
              : null;

          return ListTile(
            leading: imageBytes != null
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
            title: Text('${user['name_user']} ${user['lastname_user']}'),
            subtitle: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('$roleName\n$email\n$address'),
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
                  icon: const Icon(Icons.edit),
                  onPressed: () => showEditUserDialog(
                    user['id'],
                    user['name_user'] ?? '',
                    user['lastname_user'] ?? '',
                    user['email'] ?? '',
                    user['address_user'] ?? '',
                    user['role_user']?['id_role'] ?? 0,
                    user['activated'] ?? false,
                  ),
                ),
                IconButton(
                  icon: const Icon(Icons.delete),
                  onPressed: () => deleteUser(user['id']),
                ),
              ],
            ),
          );
        },
      ),
      floatingActionButton: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          FloatingActionButton(
            onPressed: showAddUserDialog,
            child: const Icon(Icons.add),
          ),
          const SizedBox(width: 10),
          FloatingActionButton(
            onPressed: toggleUserView,
            child: Text(
              showAllUsers ? 'Active Users' : 'See All Users',
              textAlign: TextAlign.center,
              style: const TextStyle(fontSize: 10),
            ),
          ),
        ],
      ),
    );
  }
}