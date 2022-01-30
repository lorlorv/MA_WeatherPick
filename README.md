# WeatherPick☁
### 사용자의 주변 또는 원하는 장소의 날씨를 체크할 수 있으며 <br> 날씨에 알맞은 메뉴를 랜덤으로 추천하여 식당/카페에 대한 지도와 정보를 제공하는 서비스
1. 지금 여기, 아니면 친구가 있는 저기! **날씨가 궁금**하지 않나요?
2. **오늘 날씨엔 어떤 음식**을 먹어야 잘 먹었다고 소문이 날까요?
3. **주변에 내가 원하는 메뉴**가 있는 식당만 보고싶지 않나요?
4. 즐겨찾기에 저장했던 장소를 **다시 지도**에서 보고싶지 않나요?

### → 그렇다면, 날씨가 골라주는 `WeatherPick☁`을 이용해보세요!

- 배달에 집중되어 있던 소비 패턴을 주변 동네 상권으로 분산시킵니다.
- 사용자는 궂은 날씨 때 멀리 나가지 않고, 방황하지 않고도 날씨에 안성맞춤인 메뉴의 식당과 그 주변의 카페까지 한번에 알 수 있습니다.

<br>

## 프로젝트 개요
* 2021/09/01 ~ 2021/12/26
* Java / Android
* 1인 프로젝트 
* `demo Video` : <https://drive.google.com/file/d/1P5TGPWvZmnv2D6rBXF_nVq2n0jJF2hBV/view?usp=sharing/>

<br>

## 프로젝트 기술 스택 
- **공공데이터 오픈 API 및 Google Map, GPS 사용 :** 현재 위치/원하는 위치에 해당하는 날씨 정보 확인 가능
- 날씨에 맞는 foodList 中 **랜덤으로 추천**
- **Google Map API &**  : 추천 메뉴에 해당하는 식당 위치를 선택한 위치 주변 지도에 표시
- **Google Places API** : 선택한 장소의 상세정보 표시
- **SQLite** : 원하는 장소 정보 즐겨찾기 저장 및 리뷰 CRUD 기능
- **지도 화면 캡처 후 MMS로 전송** : 주변에 공유
- **notification 알람 기능** : 오늘의 날씨를 notification으로 알람해줌
- **UI/UX**
    - splash 화면 : 초기 로딩 화면
    - drawer nav bar : 모든 Activity에서 접근 가능한 메뉴
    
<br>

## 프로젝트 예시 

<br>

## 프로젝트 회고록
* `notion` : <https://lorlorv.notion.site/WeatherPick-32b9856e1221435f802f8a9eac8854ba/>
