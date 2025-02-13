package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.CompositeKey.AppointmentCompositeKey;
import com.example.GraduationProject.Common.Entities.Appointment;
import com.example.GraduationProject.Common.Entities.AppointmentNotification;
import com.example.GraduationProject.Core.Repositories.AppointmentNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentNotificationService {

    private final AppointmentNotificationRepository notificationRepository;


    public void createNotification(Appointment appointment) {
        // استخراج وقت البداية فقط من نص الوقت (مثلاً: "14:20 - 14:40" → "14:20")
        String[] times = appointment.getAppointmentTime().split(" - ");
        String startTimeStr = times[0].trim(); // "14:20"

        // تحويل وقت البداية إلى LocalTime باستخدام تنسيق محدد
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime appointmentStartTime = LocalTime.parse(startTimeStr, timeFormatter);

        // حساب التاريخ والوقت قبل 24 ساعة
        LocalDate notificationDate = appointment.getAppointmentDate().minusDays(1);
        String notificationTime = appointmentStartTime.format(timeFormatter); // تنسيق الوقت بنفس الشكل

        AppointmentNotification notification = AppointmentNotification.builder()
                .appointmentID(appointment.getAppointmentID())
                .doctorID(appointment.getDoctorID())
                .patientID(appointment.getPatientID())
                .clinicID(appointment.getClinicID())
                .date(notificationDate)
                .time(notificationTime) // حفظ وقت الإشعار الجديد بنفس التنسيق
                .isSend(false)
                .build();

        notificationRepository.save(notification);
    }



//    @Scheduled(fixedRate = 3600000) // تشغيلها كل ساعة
//    public void sendNotifications() {
//        LocalDate today = LocalDate.now();
//        LocalTime now = LocalTime.now();
//
//        // ضبط التنسيق لضمان تطابقه مع البيانات المخزنة
//        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
//        String formattedTime = now.format(timeFormatter);
//
//        List<AppointmentNotification> notifications = notificationRepository
//                .findByDateAndTimeAndIsSendFalse(today, formattedTime);
//
//        for (AppointmentNotification notification : notifications) {
//            sendNotificationToUser(notification);
//            notification.setIsSend(true);
//            notificationRepository.save(notification);
//        }
//    }


    // ميثود لإرسال الإشعارات (عبر Firebase أو SMS أو Email)
    private void sendNotificationToUser(AppointmentNotification notification) {
        System.out.println("📢 إرسال إشعار للمريض رقم: " + notification.getPatientID() +
                " بموعده غداً في الساعة " + notification.getTime());
    }


}
