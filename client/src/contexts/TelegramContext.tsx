import { createContext, useContext, useEffect, useState, ReactNode } from "react";

interface TelegramUser {
  id: number;
  first_name: string;
  last_name?: string;
  username?: string;
  language_code?: string;
}

interface TelegramContextType {
  user: TelegramUser | null;
  webApp: any;
  isReady: boolean;
}

const TelegramContext = createContext<TelegramContextType>({
  user: null,
  webApp: null,
  isReady: false,
});

export function TelegramProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<TelegramUser | null>(null);
  const [webApp, setWebApp] = useState<any>(null);
  const [isReady, setIsReady] = useState(false);

  useEffect(() => {
    const tg = (window as any).Telegram?.WebApp;
    
    if (tg) {
      tg.ready();
      tg.expand();
      setWebApp(tg);

      const telegramUser = tg.initDataUnsafe?.user;
      if (telegramUser) {
        setUser({
          id: telegramUser.id,
          first_name: telegramUser.first_name,
          last_name: telegramUser.last_name,
          username: telegramUser.username,
          language_code: telegramUser.language_code,
        });
      } else {
        console.warn("Telegram user not found in development mode, using mock user");
        setUser({
          id: 123456789,
          first_name: "Test",
          last_name: "User",
          username: "testuser",
          language_code: "ru",
        });
      }
      setIsReady(true);
    } else {
      console.warn("Telegram WebApp not available, using mock user for development");
      setUser({
        id: 123456789,
        first_name: "Test",
        last_name: "User",
        username: "testuser",
        language_code: "ru",
      });
      setIsReady(true);
    }
  }, []);

  return (
    <TelegramContext.Provider value={{ user, webApp, isReady }}>
      {children}
    </TelegramContext.Provider>
  );
}

export function useTelegram() {
  return useContext(TelegramContext);
}
