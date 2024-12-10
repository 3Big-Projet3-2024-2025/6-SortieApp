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
      title: 'User Management',
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
      final response = await http.get(Uri.parse(apiUrl));
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
      String email_user, String address_user, int id_role) async {
    try {
      final response = await http.post(
        Uri.parse(apiUrl),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'name_user': name_user,
          'lastname_user': lastname_user,
          'email_user': email_user,
          'address_user': address_user,
          'role_user': {'id_role': id_role},
          'activated_user': true, // Default value for new users
        }),
      );
      if (response.statusCode == 200) {
        fetchUsers();
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  Future<void> updateUser(int idUser, String name_user, String lastname_user,
      String email_user, String address_user, int id_role) async {
    try {
      final response = await http.put(
        Uri.parse('$apiUrl/$idUser'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'name_user': name_user,
          'lastname_user': lastname_user,
          'email_user': email_user,
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

  Future<void> deleteUser(int idUser) async {
    try {
      final response = await http.delete(Uri.parse('$apiUrl/$idUser'));
      if (response.statusCode == 200) {
        fetchUsers();
      }
    } catch (e) {
      print('Error: $e');
    }
  }

  void showAddUserDialog() async {
    final TextEditingController nameController = TextEditingController();
    final TextEditingController lastnameController = TextEditingController();
    final TextEditingController emailController = TextEditingController();
    final TextEditingController addressController = TextEditingController();

    int? selectedRoleId;

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Add User'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
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
                if (selectedRoleId != null) {
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
  }

  void showEditUserDialog(
      int idUser,
      String currentName,
      String currentLastname,
      String currentEmail,
      String currentAddress,
      int currentRoleId,
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

    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Edit User'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
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
                if (selectedRoleId != null) {
                  updateUser(
                    idUser,
                    nameController.text,
                    lastnameController.text,
                    emailController.text,
                    addressController.text,
                    selectedRoleId!,
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
          final email = user['email_user'] ?? 'No Email';

          return ListTile(
            title: Text('${user['name_user']} ${user['lastname_user']}'),
            subtitle: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('$roleName\n$email\n$address'),
                const SizedBox(height: 5),
                Row(
                  children: [
                    const Text('Active: '),
                    Text(
                      user['activated_user'] == true ? 'Yes' : 'No',
                      style: TextStyle(
                        color: user['activated_user'] == true
                            ? Colors.green
                            : Colors.red,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ],
                ),
              ],
            ),
            trailing: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                IconButton(
                  icon: const Icon(Icons.edit),
                  onPressed: () => showEditUserDialog(
                    user['id_user'],
                    user['name_user'] ?? '',
                    user['lastname_user'] ?? '',
                    user['email_user'] ?? '',
                    user['address_user'] ?? '',
                    user['role_user']?['id_role'] ?? 0,
                  ),
                ),
                IconButton(
                  icon: const Icon(Icons.delete),
                  onPressed: () => deleteUser(user['id_user']),
                ),
              ],
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: showAddUserDialog,
        child: const Icon(Icons.add),
      ),
    );
  }
}
