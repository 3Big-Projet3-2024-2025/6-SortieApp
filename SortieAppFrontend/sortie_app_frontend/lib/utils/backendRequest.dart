import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter/foundation.dart';
import 'package:sortie_app_frontend/utils/tokenUtils.dart';

String getBackendUrl() {
  return kIsWeb ? 'http://localhost:8081' : 'http://192.168.0.12:8081';
}

Future<Map<String, String>> getHeader() async {
  String? accessToken = await getAccesToken();

  if (accessToken == null || isTokenExpired(accessToken)) {
    accessToken = await refreshToken();
  }

  if (accessToken == null) {
    throw Exception("Unable to retrieve a valid access token");
  }

  return {
    'Authorization': 'Bearer $accessToken',
    'Content-Type': 'application/json',
  };
}

/// Vérifie si le token est expiré
bool isTokenExpired(String token) {
  try {
    final parts = token.split('.');
    if (parts.length != 3) {
      return true; // Le token est invalide
    }

    // Décoder la charge utile (payload) du token
    final payload = parts[1];
    final normalizedPayload = base64Url.normalize(payload);
    final decodedPayload = utf8.decode(base64Url.decode(normalizedPayload));
    final payloadMap = json.decode(decodedPayload);

    // Vérifiez la date d'expiration "exp"
    final exp = payloadMap['exp'];
    if (exp == null) {
      return true;
    }

    // Comparer avec l'heure actuelle
    final currentTime = DateTime.now().millisecondsSinceEpoch ~/ 1000;
    return exp < currentTime;
  } catch (e) {
    // En cas d'erreur, considérer que le token est expiré
    return true;
  }
}

/// Récupère un nouveau token en utilisant le refresh token
Future<String?> refreshToken() async {
  final refreshToken = await getRefreshToken();
  if (refreshToken == null) {
    return null;
  }

  // Appeler l'API pour rafraîchir le token
  final response = await http.post(
    Uri.parse('https://example.com/api/token/refresh'),
    headers: {'Content-Type': 'application/json'},
    body: json.encode({'refresh_token': refreshToken}),
  );

  if (response.statusCode == 200) {
    final data = json.decode(response.body);
    final newAccessToken = data['access_token'];
    final newRefreshToken = data['refresh_token'];

    // Sauvegarder les nouveaux tokens
    await refreshTokens(newAccessToken, newRefreshToken);

    return newAccessToken;
  } else {
    // Gestion des erreurs de rafraîchissement
    throw Exception("Failed to refresh token");
  }
}