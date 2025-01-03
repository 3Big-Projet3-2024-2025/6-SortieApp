import 'dart:convert';
import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get/get_core/src/get_main.dart';
import 'package:image_picker/image_picker.dart';
import 'package:http/http.dart' as http;
import 'package:sortie_app_frontend/pages/qrcode_page.dart';
import 'package:sortie_app_frontend/utils/backendRequest.dart';
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
    final String url = "${getBackendUrl()}/users/profile";
    try {
      var header = await getHeader();
      final response = await http.get(
        Uri.parse(url),
        headers: header,
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

  Future<void> _pickImageAndUpload() async {
    final ImagePicker picker = ImagePicker();
    final XFile? image = await picker.pickImage(source: ImageSource.gallery);

    if (image != null) {
      try {
        final Uint8List imageBytes = await image.readAsBytes();
        final String base64Image = base64Encode(imageBytes);

        await _updateProfilePicture(base64Image);
      } catch (e) {
        print('Error picking or uploading image: $e');
      }
    }
  }

  Future<void> _updateProfilePicture(String base64Image) async {
    final String url = '${getBackendUrl()}/users/updateProfilePicture';
    final header = await getHeader();

    try {
      final response = await http.put(
        Uri.parse(url),
        headers: header,
        body: json.encode({'picture_user': base64Image}),
      );

      if (response.statusCode == 200) {
        setState(() {
          userData!['picture_user'] = base64Image;
        });
      } else {
        throw Exception('Failed to update profile picture: ${response.statusCode}');
      }
    } catch (e) {
      print('Error updating profile picture: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Sortie\'App'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () async {
              await secureStorage.deleteAll();
              Get.offNamed('/login');
            },
          ),
        ],
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
              child: Stack(
                alignment: Alignment.bottomRight,
                children: [
                  CircleAvatar(
                    radius: 80,
                    backgroundImage: _getProfileImage(),
                  ),
                  Positioned(
                    bottom: 5,
                    right: 5,
                    child: GestureDetector(
                      onTap: () => showDialog(
                        context: context,
                        builder: (BuildContext context) {
                          return AlertDialog(
                            title: const Text("Update Profile Picture"),
                            content: const Text("Choose an image from your gallery."),
                            actions: [
                              TextButton(
                                onPressed: () {
                                  Navigator.of(context).pop();
                                },
                                child: const Text("Cancel"),
                              ),
                              TextButton(
                                onPressed: () async {
                                  Navigator.of(context).pop();
                                  await _pickImageAndUpload();
                                },
                                child: const Text("Choose Image"),
                              ),
                            ],
                          );
                        },
                      ),
                      child: Container(
                        height: 40,
                        width: 40,
                        decoration: BoxDecoration(
                          color: Theme.of(context).primaryColor,
                          shape: BoxShape.circle,
                        ),
                        child: const Icon(
                          Icons.edit,
                          color: Colors.white,
                        ),
                      ),
                    ),
                  ),
                ],
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
            /*const SizedBox(height: 10),
            ProfileField(
              label: 'Role',
              value: userData!['role_user']?['name_role'] ?? 'N/A',
            ),*/
            ElevatedButton(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => const QRCodePage()),
                );
              },
              child: const Text('Show Qr Code'),
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
