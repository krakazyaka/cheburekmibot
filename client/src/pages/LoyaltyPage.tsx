import LoyaltyCard from "@/components/LoyaltyCard";
import { useQuery } from "@tanstack/react-query";
import { useState, useEffect } from "react";
import { useTelegram } from "@/contexts/TelegramContext";
import type { LoyaltyCard as LoyaltyCardType, User } from "@shared/schema";

export default function LoyaltyPage() {
  const { user: telegramUser } = useTelegram();
  const [currentUser, setCurrentUser] = useState<User | null>(null);

  useEffect(() => {
    if (telegramUser && !currentUser) {
      fetch("/api/users/find-or-create", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          telegramId: telegramUser.id.toString(),
          firstName: telegramUser.first_name,
          lastName: telegramUser.last_name,
          username: telegramUser.username,
        }),
      })
        .then((res) => res.json())
        .then((user) => setCurrentUser(user))
        .catch((error) => console.error("Failed to find/create user:", error));
    }
  }, [telegramUser, currentUser]);

  const { data: loyaltyCard } = useQuery<LoyaltyCardType>({
    queryKey: currentUser ? [`/api/loyalty/${currentUser.id}`] : [],
    enabled: !!currentUser,
  });

  const current = loyaltyCard?.current || 0;
  const target = loyaltyCard?.target || 10;
  const freeAvailable = loyaltyCard?.freeAvailable || 0;

  return (
    <div className="min-h-screen pb-24">
      <header className="sticky top-0 z-30 bg-background/95 backdrop-blur-md border-b">
        <div className="max-w-lg mx-auto px-4 py-4">
          <h1 className="text-2xl font-semibold" data-testid="text-page-title">
            Программа лояльности
          </h1>
          <p className="text-sm text-muted-foreground">
            Отслеживайте награды и получайте бесплатные чебуреки
          </p>
        </div>
      </header>

      <div className="max-w-lg mx-auto px-4 py-6 space-y-6">
        <LoyaltyCard current={current} target={target} freeAvailable={freeAvailable} />

        <div className="bg-muted/30 rounded-lg p-4">
          <h3 className="font-semibold mb-2">Как это работает</h3>
          <ul className="text-sm text-muted-foreground space-y-2">
            <li>• Заказывайте чебуреки онлайн или офлайн</li>
            <li>• Бонусы начисляются автоматически после завершения заказа</li>
            <li>• Каждый {target}-й чебурек бесплатно!</li>
            <li>• Следите за прогрессом в вашей карте лояльности</li>
          </ul>
        </div>
      </div>
    </div>
  );
}
