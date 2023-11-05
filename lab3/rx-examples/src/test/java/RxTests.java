import static util.ColorUtil.print;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observables.ConnectableObservable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import util.Color;

public class RxTests {

  private static final String MOVIES1_DB = "movies1";

  private static final String MOVIES2_DB = "movies2";

  /** Example 1: Creating and subscribing observable from iterable. */
  @Test
  public void loadMoviesAsList() throws FileNotFoundException {
    MovieReader reader = new MovieReader();
    reader.getMoviesFromList(MOVIES1_DB).subscribe(System.out::println);
  }

  /** Example 2: Creating and subscribing observable from custom emitter. */
  @Test
  public void loadMoviesAsStream() {
    MovieReader reader = new MovieReader();
    reader.getMoviesAsStream(MOVIES1_DB).subscribe(System.out::println);
  }

  /** Example 3: Handling errors. */
  @Test
  public void loadMoviesAsStreamAndHandleError() {
    MovieReader reader = new MovieReader();
    reader
        .getMoviesAsStream(MOVIES1_DB + "invalidFilename")
        .subscribe(
            movie -> print(movie, Color.BLUE),
            throwable -> print("error: " + throwable, Color.RED_BOLD));
  }

  /** Example 4: Signaling end of a stream. */
  @Test
  public void loadMoviesAsStreamAndFinishWithMessage() {
    MovieReader reader = new MovieReader();
    reader
        .getMoviesAsStream(MOVIES1_DB)
        .subscribe(
            movie -> print(movie, Color.BLUE),
            throwable -> print("error: " + throwable, Color.RED_BOLD),
            () -> print("Stream done", Color.GREEN_BACKGROUND_BRIGHT));
  }

  /** Example 5: Filtering stream data. */
  @Test
  public void displayLongMovies() {
    MovieReader reader = new MovieReader();
    reader
        .getMoviesAsStream(MOVIES1_DB)
        .filter(movie -> movie.getLength() > 160)
        .subscribe(movie -> print(movie, Color.BLUE));
  }

  /** Example 6: Transforming stream data. */
  @Test
  public void displaySortedMoviesTitles() {
    MovieReader reader = new MovieReader();
    reader
        .getMoviesAsStream(MOVIES1_DB)
        .map(movie -> movie.getDescription())
        .take(5)
        .sorted()
        .take(3)
        .subscribe(description -> print(description, Color.BLUE));
  }

  /** Example 7: Monads are like burritos. */
  @Test
  public void displayActorsForMovies() {
    MovieReader reader = new MovieReader();
    reader
        .getMoviesAsStream(MOVIES1_DB)
        .flatMap(movie -> Observable.fromIterable(reader.readActors(movie)))
        .distinct()
        .subscribe(actor -> print(actor, Color.GREEN_BACKGROUND));
  }

  /** Example 8: Combining observables. */
  @Test
  public void loadMoviesFromManySources() {
    MovieReader reader = new MovieReader();
    Observable<Movie> observable1 =
        reader
            .getMoviesAsStream(MOVIES1_DB)
            .doOnNext(movie -> print(movie.getTitle(), Color.RED_BOLD));
    Observable<Movie> observable2 = reader.getMoviesAsStream(MOVIES2_DB);

    Observable.merge(observable1, observable2).subscribe(System.out::println);
  }

  /** Example 9: Playing with threads (subscribeOn). */
  @Test
  public void loadMoviesInBackground() throws InterruptedException {
    MovieReader reader = new MovieReader();
    reader
        .getMoviesAsStream(MOVIES1_DB)
        .doOnNext(movie -> print(Thread.currentThread().getId(), Color.RED))
        .subscribeOn(Schedulers.io()) // order doesn't matter here
        .subscribe(movie -> print(movie.getTitle(), Color.GREEN));

    Thread.sleep(10000);
  }

  /** Example 10: Playing with threads (observeOn). */
  @Test
  public void switchThreadsDuringMoviesProcessing() throws InterruptedException {
    MovieReader movieReader = new MovieReader();

    movieReader
        .getMoviesAsStream(MOVIES1_DB)
        .take(40)
        .subscribeOn(Schedulers.io())
        .doOnNext(movie -> print(movie.getTitle(), Color.BLACK_BACKGROUND_BRIGHT))
        .observeOn(Schedulers.io())
        .blockingSubscribe(movie -> print(movie.getTitle(), Color.BLUE_BACKGROUND_BRIGHT));
  }

  /** Example 11: Combining parallel streams. */
  @Test
  public void loadMoviesFromManySourcesParallel() {
    // Static merge solution:
    MovieReader reader = new MovieReader();
    Observable<Movie> observable1 =
        reader
            .getMoviesAsStream(MOVIES1_DB)
            .subscribeOn(Schedulers.io())
            .doOnNext(movie -> print(movie.getTitle(), Color.RED));
    Observable<Movie> observable2 =
        reader
            .getMoviesAsStream(MOVIES2_DB)
            .subscribeOn(Schedulers.io())
            .doOnNext(movie -> print(movie.getTitle(), Color.MAGENTA));

    // so far observables are just set up and configured

    Observable.merge(observable1, observable2)
        .blockingSubscribe(movie -> print(movie.getTitle(), Color.GREEN_BOLD));

    // FlatMap solution:
    final MovieDescriptor movie1Descriptor = new MovieDescriptor(MOVIES1_DB, Color.GREEN);
    final MovieDescriptor movie2Descriptor = new MovieDescriptor(MOVIES2_DB, Color.BLUE);

    Observable.just(movie1Descriptor, movie2Descriptor)
        .flatMap(
            movieDescriptor ->
                reader
                    .getMoviesAsStream(movieDescriptor.movieDbFilename)
                    .subscribeOn(Schedulers.io())
                    .doOnNext(movie -> print(movie.getTitle(), movieDescriptor.debugColor)))
        .blockingSubscribe(movie -> print(movie, Color.BLACK));
  }

  /** Example 12: Zip operator. */
  @Test
  public void loadMoviesWithDelay() {
    MovieReader reader = new MovieReader();

    Observable<Movie> moviesStream = reader.getMoviesAsStream(MOVIES1_DB);
    Observable<Long> interval = Observable.interval(1, TimeUnit.SECONDS);

    Observable.zip(interval, moviesStream, (time, movie) -> time + " " + movie.getTitle())
        .take(10)
        .blockingSubscribe(s -> print(s, Color.RED_BOLD));
  }

  /** Example 13: Backpressure. */
  @Test
  public void trackMoviesLoadingWithBackpressure() throws InterruptedException {
    MovieReader reader = new MovieReader();
    reader
        .getMoviesAsStream(MOVIES1_DB)
        .subscribeOn(Schedulers.io())
        .doOnNext(movie -> Thread.sleep(5))
        .toFlowable(BackpressureStrategy.LATEST)
        .observeOn(Schedulers.io(), true, 1)
        .doOnNext(this::displayProgress)
        .blockingSubscribe(); // ignore results
  }

  /** Example 14: Cold and hot observables. */
  @Test
  public void oneMovieStreamManyDifferentSubscribers() {
    MovieReader movieReader = new MovieReader();

    // Cold observable
    Observable<Movie> coldObservable = movieReader.getMoviesAsStream(MOVIES1_DB);

    // query 1
    coldObservable
        .filter(movie -> movie.getRating().equals("G"))
        .take(10)
        .subscribe(movie -> print(movie, Color.RED_BACKGROUND));

    // query 2
    coldObservable
        .sorted(Comparator.comparingInt(Movie::getLength))
        .subscribeOn(Schedulers.io())
        .take(5)
        .blockingSubscribe(movie -> print(movie, Color.BLUE_BACKGROUND));

    System.out.println("");

    // Hot observable
    // Data is loaded only once
    ConnectableObservable<Movie> hotObservable = coldObservable.publish();

    // query 1
    hotObservable
        .filter(movie -> movie.getRating().equals("G"))
        .take(10)
        .subscribe(movie -> print(movie, Color.RED_BACKGROUND));

    // query 2
    hotObservable
        .filter(movie -> movie.getLength() < 150)
        .take(5)
        .subscribe(movie -> print(movie, Color.BLUE_BACKGROUND));
    hotObservable.connect();
  }

  /** Example 15: Caching observables (hot-cold hybrid). */
  @Test
  public void cacheMoviesInfo() {
    MovieReader reader = new MovieReader();
    Observable<Movie> testObservable = reader.getMoviesAsStream(MOVIES1_DB).take(50).cache();

    testObservable.subscribe(movie -> print(movie, Color.RED_BACKGROUND));
    System.out.println(testObservable.count().blockingGet());
  }

  private void displayProgress(Movie movie) throws InterruptedException {
    print((movie.getIndex() / 500.0 * 100) + "%", Color.GREEN);
    Thread.sleep(50);
  }

  private class MovieDescriptor {
    private final String movieDbFilename;

    private final Color debugColor;

    private MovieDescriptor(String movieDbFilename, Color debugColor) {
      this.movieDbFilename = movieDbFilename;
      this.debugColor = debugColor;
    }

    public Color getDebugColor() {
      return debugColor;
    }

    public String getMovieDbFilename() {
      return movieDbFilename;
    }
  }
}
