interface SkeletonCardsProps {
  count?: number
  variant?: 'events' | 'tickets' | 'admin'
}

export function SkeletonCards({
  count = 4,
  variant = 'events',
}: SkeletonCardsProps) {
  return (
    <section
      className={
        variant === 'tickets'
          ? 'ticketsGrid'
          : variant === 'admin'
            ? 'adminEventsList'
            : 'eventsGrid'
      }
    >
      {Array.from({ length: count }).map((_, index) => (
        <article className="skeletonCard" key={index}>
          <div className="skeletonLine short" />
          <div className="skeletonLine title" />
          <div className="skeletonLine" />
          <div className="skeletonLine medium" />

          <div className="skeletonFooter">
            <div className="skeletonPill" />
            <div className="skeletonButton" />
          </div>
        </article>
      ))}
    </section>
  )
}