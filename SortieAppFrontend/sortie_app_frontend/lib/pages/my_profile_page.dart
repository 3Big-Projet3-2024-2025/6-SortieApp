import 'dart:convert';
import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:sortie_app_frontend/utils/tokenUtils.dart';

class MyProfile extends StatefulWidget {
  const MyProfile({Key? key}) : super(key: key);

  @override
  State<MyProfile> createState() => _MyProfileState();
}

class _MyProfileState extends State<MyProfile> {
  Map<String, dynamic>? userData;
  bool isLoading = true;

  @override
  void initState() {
    super.initState();
    fetchUserProfile();
  }

  Future<void> fetchUserProfile() async {
    final String url = 'http://10.0.2.2:8081/users/profile';
    final String? accessToken = await getAccesToken();
    if (accessToken != null) {
      print("Access Token: $accessToken");
    } else {
      print("No Access Token found.");
    }

    try {
      final response = await http.get(
        Uri.parse(url),
        headers: {
          'Authorization': 'Bearer $accessToken',
        },
      );

      if (response.statusCode == 200) {
        setState(() {
          userData = json.decode(response.body);
          isLoading = false;
        });
      } else {
        setState(() {
          isLoading = false;
        });
        throw Exception('Failed to load profile: ${response.statusCode}');
      }
    } catch (e) {
      setState(() {
        isLoading = false;
      });
      print('Error fetching user profile: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('My Profile'),
      ),
      body: isLoading
          ? const Center(child: CircularProgressIndicator())
          : userData == null
          ? const Center(child: Text('Failed to load user data.'))
          : Padding(
        padding: const EdgeInsets.all(16.0),
        child: ListView(
          children: [
            Center(
              child: CircleAvatar(
                radius: 60,
                backgroundImage: _getProfileImage(),
              ),
            ),
            const SizedBox(height: 20),
            ProfileField(
              label: 'Last Name',
              value: userData!['lastname_user'] ?? '',
            ),
            const SizedBox(height: 10),
            ProfileField(
              label: 'First Name',
              value: userData!['name_user'] ?? '',
            ),
            const SizedBox(height: 10),
            ProfileField(
              label: 'Email',
              value: userData!['email'] ?? '',
            ),
            const SizedBox(height: 10),
            ProfileField(
              label: 'Address',
              value: userData!['address_user'] ?? '',
            ),
            const SizedBox(height: 10),
            ProfileField(
              label: 'School',
              value: userData!['school_user']?['name_school'] ?? 'N/A',
            ),
            const SizedBox(height: 10),
            ProfileField(
              label: 'Role',
              value: userData!['role_user']?['name_role'] ?? 'N/A',
            ),
          ],
        ),
      ),
    );
  }

  ImageProvider _getProfileImage() {
    final String? pictureUser = userData?['picture_user'];

    if (pictureUser != null && pictureUser.isNotEmpty) {
      try {
        final Uint8List imageBytes = base64Decode(pictureUser);
        return MemoryImage(imageBytes);
      } catch (e) {
        print('Invalid Base64 image, using default: $e');
      }
    }

    return const AssetImage('assets/images/default_profile.jpg');
  }
}

class ProfileField extends StatelessWidget {
  final String label;
  final String value;

  const ProfileField({Key? key, required this.label, required this.value}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          label,
          style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
        ),
        const SizedBox(height: 5),
        TextFormField(
          initialValue: value,
          readOnly: true,
          decoration: const InputDecoration(
            border: OutlineInputBorder(),
            filled: true,
            fillColor: Colors.grey,
          ),
        ),
      ],
    );
  }
}
