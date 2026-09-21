import UIKit
import UserNotifications
import ComposeApp

/// Kullanıcı bir local notification'a dokunduğunda uygulamayı ilgili bilginin
/// detay sayfasına yönlendirmek için Compose tarafına fact id'sini iletir.
class NotificationDelegate: NSObject, UNUserNotificationCenterDelegate {
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        if let factId = response.notification.request.content.userInfo["extra_fact_id"] as? String {
            MainViewControllerKt.setPendingFactId(factId: factId)
        }
        completionHandler()
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .sound, .list])
    }
}
