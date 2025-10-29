Тайлан

Хийсэн өөрчлөлтүүд
• JUnit 4 нэгжийн тестүүд нэмсэн:
CalendarTest, PersonTest, RoomTest, OrganizationTest, MeetingTest
• Ant build.xml нэмсэн ба дараах target-уудтай:
clean, compile, compile-tests, test, javadoc
• README.md шинэчилж, ажиллуулах заавар болон тайланг нэмэв

Тестийн хамрах хүрээ ба тоо
• Нийт тест: 21
• CalendarTest – 9
• PersonTest – 2
• RoomTest – 2
• OrganizationTest – 4
• MeetingTest – 4

Илрүүлсэн ба зассан алдаанууд
• Calendar.checkTimes: 12-р сарыг буруу шалгаж байсан (>=12). Одоо 12-ыг зөвшөөрөхөөр (>12) зассан.
• Calendar.isBusy болон Calendar.addMeeting: Зөвхөн эхлэл/төгсгөлийн давхцлыг шалгадаг байсан тул “бүслэх” (enclosure) болон хүрэлцэх (touching) давхцлуудыг алгасдаг байсан. Одоо цагийн огтлолцлыг start <= existingEnd && end >= existingStart нөхцлөөр бүрэн шалгадаг.

Ажиллуулах
• Нэгжийн тест:

ant test

(Тайлан гарна: build/reports)

    •	Javadoc үүсгэх:

ant javadoc

(Гаралт: build/javadoc)

Санал болгох сайжруулалт
• Одон, сар, өдрүүдийн хүчинтэй байдлыг бүрэн шалгах (сар бүрийн өдөр, өндрийн жил гэх мэт) — одоогоор “Day does not exist” hack ашигласан.
• Meeting дээр attendees болон room-ийн null хамгаалалт нэмэх, builder эсвэл validation оруулах.
