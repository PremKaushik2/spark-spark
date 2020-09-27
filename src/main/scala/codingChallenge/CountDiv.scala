package codingChallenge

object CountDiv extends App {
  /*
    Write a function:
    object Solution { def solution(A: Int, B: Int, K: Int): Int }
    that, given three integers A, B and K, returns the number of integers within the range [A..B]
     that are divisible by K, i.e.:
    { i : A ≤ i ≤ B, i mod K = 0 }
    For example, for A = 6, B = 11 and K = 2, your function should return 3,
     because there are three numbers divisible by 2 within the range [6..11], namely 6, 8 and 10.
    Assume that:
    A and B are integers within the range [0..2,000,000,000];
    K is an integer within the range [1..2,000,000,000];
    A ≤ B.
    Complexity:
    expected worst-case time complexity is O(1);
    expected worst-case space complexity is O(1).
     */


  def solution(A: Int, B: Int, K: Int): Int = (B - A + 1) / K

  //
  def solution2(A: Int, B: Int, K: Int): Int = {
    def incr(i: Int) = if (i % K == 0) 1 else 0

    def solve(count: Int, i: Int): Int = {
      if (i == B) count + incr(i)
      else solve(count + incr(i), i + 1)
    }

    solve(0, A)
  }

  solution(6, 11, 2)
  solution(11, 19, 4)
  solution(35, 37, 2)
  solution(12, 26, 3)
  solution(0, 11, 1) // wrong should be 11
  solution(3444, 2000000000, 1234586)
  solution2(6, 11, 2)
  solution2(11, 19, 4)
  solution2(35, 37, 2)
  solution2(12, 26, 3)
  solution2(0, 11, 1)
  solution2(0, 1, 11)

  solution2(0, 0, 11) // wrong should be 1
  solution2(10, 10, 5) // wrong should be 1
  solution2(10, 10, 7) // wrong should be 1
  solution2(10, 10, 20) // wrong should be 1
}
