import CodeEntryCard from "../CodeEntryCard";

export default function CodeEntryCardExample() {
  return (
    <div className="p-4 max-w-md">
      <CodeEntryCard onSubmit={(code) => console.log("Code submitted:", code)} />
    </div>
  );
}
