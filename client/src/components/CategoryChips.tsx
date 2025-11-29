import { Badge } from "@/components/ui/badge";
import { UtensilsCrossed, Package, Cookie, Coffee } from "lucide-react";

interface CategoryChipsProps {
  selectedCategory: string;
  onSelectCategory: (category: string) => void;
}

export default function CategoryChips({
  selectedCategory,
  onSelectCategory,
}: CategoryChipsProps) {
  const categories = [
    { id: "CHEBUR", label: "Чебуреки", icon: UtensilsCrossed },
    { id: "COMBO", label: "Комбо", icon: Package },
    { id: "PIROG", label: "Чебуреки-пироги", icon: Cookie },
    { id: "DRINKS", label: "Напитки", icon: Coffee },
  ];

  return (
    <div className="flex gap-2 overflow-x-auto pb-2 px-4 scrollbar-hide">
      {categories.map((category) => {
        const Icon = category.icon;
        const isActive = selectedCategory === category.id;
        return (
          <Badge
            key={category.id}
            variant={isActive ? "default" : "secondary"}
            className="flex items-center gap-2 px-4 py-2 cursor-pointer whitespace-nowrap hover-elevate active-elevate-2"
            onClick={() => onSelectCategory(category.id)}
            data-testid={`button-category-${category.id}`}
          >
            <Icon className="w-4 h-4" />
            <span className="font-medium">{category.label}</span>
          </Badge>
        );
      })}
    </div>
  );
}
