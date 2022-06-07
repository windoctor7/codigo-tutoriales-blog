package windoctor7.github.io.spring5.reactive.kotlin

/**
 * Created by Ascari Q. Romo Pedraza - molder.itp@gmail.com on 24/10/2017.
 */
data class UserResultGh( //clase envoltorio para github
        val total_count:Int,
        val incomplete_results:Boolean,
        val items:ArrayList<User>)
