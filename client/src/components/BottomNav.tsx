import { Home, Gift, ShoppingBag, User } from "lucide-react";
import { Link, useLocation } from "wouter";

export default function BottomNav() {
  const [location] = useLocation();

  const tabs = [
    { icon: Home, label: "Меню", path: "/" },
    { icon: Gift, label: "Бонусы", path: "/loyalty" },
    { icon: ShoppingBag, label: "Заказы", path: "/orders" },
    { icon: User, label: "Профиль", path: "/profile" },
  ];

  return (
    <nav className="fixed bottom-0 left-0 right-0 z-50 bg-background/95 backdrop-blur-md border-t">
      <div className="flex items-center justify-around h-16 max-w-lg mx-auto px-2">
        {tabs.map((tab) => {
          const Icon = tab.icon;
          const isActive = location === tab.path;
          return (
            <Link
              key={tab.path}
              href={tab.path}
              data-testid={`link-${tab.label.toLowerCase()}`}
            >
              <button
                className={`flex flex-col items-center justify-center gap-1 px-4 py-2 rounded-md transition-colors ${
                  isActive
                    ? "text-primary"
                    : "text-muted-foreground hover-elevate"
                }`}
                data-testid={`button-nav-${tab.label.toLowerCase()}`}
              >
                <Icon className={`w-5 h-5 ${isActive ? "fill-current" : ""}`} />
                <span className="text-xs font-medium">{tab.label}</span>
              </button>
            </Link>
          );
        })}
      </div>
    </nav>
  );
}
