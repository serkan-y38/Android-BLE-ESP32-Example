#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>

BLECharacteristic *pCharacteristic;
bool deviceConnected = false;

#define SERVICE_UUID "22bf526e-1f59-40fb-a344-0bea8c1bfef2"
#define CHARACTERISTIC_UUID "cdc7651d-88bd-4c0d-8c90-4572db5aa14b"

class MyServerCallbacks: public BLEServerCallbacks { 
  void onConnect(BLEServer* pServer){
    Serial.println("connected");
    deviceConnected = true;
  };

  void onDisconnect(BLEServer *pServer){
    deviceConnected = false;
    
    Serial.println("disconnected");
    delay(500); 

    pServer->getAdvertising()->start();  
    Serial.println("waiting for connection from client");
  }
};

class CharacteristicCallback : public BLECharacteristicCallbacks {
  void onWrite(BLECharacteristic* dhtCharacteristic) {
    String value = pCharacteristic->getValue();
    Serial.print("Received Value: ");
    Serial.println(value.c_str());  
  }
};


void initialize(){
  Serial.begin(9600);
  
  BLEDevice::init("ESP32");

  BLEServer *pServer = BLEDevice::createServer();
  pServer->setCallbacks(new MyServerCallbacks());

  BLEService *pService = pServer->createService(SERVICE_UUID);  

  pCharacteristic = pService->createCharacteristic(
    CHARACTERISTIC_UUID,
    BLECharacteristic::PROPERTY_NOTIFY | 
    BLECharacteristic::PROPERTY_READ | 
    BLECharacteristic::PROPERTY_WRITE
  );

  pCharacteristic->addDescriptor(new BLE2902());
  pCharacteristic->setCallbacks(new CharacteristicCallback());

  pService->start();

  BLEAdvertising* pAdvertising = BLEDevice::getAdvertising();
  pAdvertising->addServiceUUID(SERVICE_UUID);
  pAdvertising->setScanResponse(true);
  pAdvertising->setMinPreferred(0x12);
  
  pServer->getAdvertising()->start();

  Serial.println("waiting for connection from client");
}

void setup() {
  initialize();
}

void loop() {
  if (deviceConnected) {
    int randomNumber = random(0, 100);
    char txString[8];

    dtostrf(randomNumber, 1, 2, txString);

    pCharacteristic->setValue(txString);  
    pCharacteristic->notify();

    Serial.print("sent value: ");
    Serial.println(randomNumber);

    delay(1000);
  }
}