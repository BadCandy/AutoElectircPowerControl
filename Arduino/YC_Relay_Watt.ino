#include <Time.h>
#include <SoftwareSerial.h>

#define RELAY0 11
#define RELAY1 12
#define RELAY2 13

const int _length = 3;
const int RELAY[_length] = {RELAY0, RELAY1, RELAY2};
const double wattPerSec = 0.01;
const unsigned long interval = 1000;
unsigned long startTime[_length] = {0, 0, 0};
int total_sec = 0;
int val[_length] = {0, 0, 0};
int old_val[_length] = {0, 0, 0};
int state[_length] = {1, 1, 1};
byte flag = 0;
double totalWatt = 0;

SoftwareSerial BTSerial(4, 5);    // rx, tx 설정 (교차로 아두이노 핀에 끼우기!)

void setup() {
  for (int i = 0; i < _length; i++) {
    pinMode(RELAY[i], OUTPUT);
  }
  Serial.begin(9600);
  BTSerial.begin(9600);
}

void loop() {
  while (BTSerial.available()) {    //안드로이드에서 바이트형 데이터를 아두이노에서 받는 부분
    flag = BTSerial.read();
    if (flag == 55) {
      state[0] = 0;
      state[1] = 0;
      state[2] = 0;
    }
    else if (flag == 54) {
      state[0] = 0;
      state[1] = 0;
      state[2] = 1;
    }
    else if (flag == 53) {
      state[0] = 0;
      state[1] = 1;
      state[2] = 0;
    }
    else if (flag == 52) {
      state[0] = 0;
      state[1] = 1;
      state[2] = 1;
    }
    else if (flag == 51) {
      state[0] = 1;
      state[1] = 0;
      state[2] = 0;
    }
    else if (flag == 50) {
      state[0] = 1;
      state[1] = 0;
      state[2] = 1;
    }
    else if (flag == 49) {
      state[0] = 1;
      state[1] = 1;
      state[2] = 0;
    }
    else if (flag == 48) {
      state[0] = 1;
      state[1] = 1;
      state[2] = 1;
    }
    Serial.print("\n");
    Serial.print(state[0]);
    Serial.print("-");
    Serial.print(state[1]);
    Serial.print("-");
    Serial.print(state[2]);
    Serial.print("\n");
  }
  for (int i = 0; i < _length; i++) {
    if (state[i] == 1) {
      digitalWrite(RELAY[i], HIGH); //차단기 ON
    } else {
      digitalWrite(RELAY[i], LOW);  //차단기 OFF
      if (startTime[i] + interval < millis()) {
        startTime[i] = millis();
        calSec();
      }
      delay(100
           );
      if (i == 2) {
        BTSerial.print("A");
        BTSerial.print(total_sec);
        Serial.println(total_sec);
      }
    }
  }
}

void calSec() {
  total_sec += 1;
}

