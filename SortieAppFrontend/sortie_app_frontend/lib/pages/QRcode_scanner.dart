import 'package:flutter/material.dart';
import 'package:qr_code_dart_scan/qr_code_dart_scan.dart';
import 'package:sortie_app_frontend/pages/scanner_result.dart';

class QRScannerPage extends StatelessWidget {
  const QRScannerPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Scan QR Code'),
        backgroundColor: Colors.blue,
      ),
      body: QRCodeDartScanView(
        scanInvertedQRCode: true, // Active la lecture des QR inversés
        typeScan: TypeScan.live, // Lecture en direct
        onCapture: (Result result) {
          // Naviguer vers une nouvelle page avec le résultat du scan
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => QRResultPage(userId: result.text),
            ),
          );
        },
      ),
    );
  }
}