export default function TaskCard({ task }) {
  return (
    <div>
      <h4>{task.title}</h4>
      <p>{task.status}</p>
    </div>
  );
}