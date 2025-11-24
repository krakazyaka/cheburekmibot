import { Card } from "@/components/ui/card";

interface MenuItemCardProps {
  id: string;
  name: string;
  description: string;
  price: number;
  image: string;
  onClick: () => void;
}

export default function MenuItemCard({
  name,
  description,
  price,
  image,
  onClick,
}: MenuItemCardProps) {
  return (
    <Card
      className="flex gap-3 p-4 cursor-pointer hover-elevate active-elevate-2"
      onClick={onClick}
      data-testid={`card-menu-item-${name.toLowerCase().replace(/\s+/g, "-")}`}
    >
      <img
        src={image}
        alt={name}
        className="w-20 h-20 rounded-lg object-cover flex-shrink-0"
        data-testid={`img-menu-item-${name.toLowerCase().replace(/\s+/g, "-")}`}
      />
      <div className="flex flex-col flex-1 min-w-0">
        <h3 className="font-semibold text-base leading-tight line-clamp-1" data-testid={`text-item-name`}>
          {name}
        </h3>
        <p className="text-sm text-muted-foreground line-clamp-2 mt-1 leading-tight" data-testid={`text-item-description`}>
          {description}
        </p>
        <p className="text-base font-medium text-primary mt-auto pt-2" data-testid={`text-item-price`}>
          {price.toFixed(2)}₽
        </p>
      </div>
    </Card>
  );
}
