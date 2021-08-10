package kr.co.healthcare.game;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Random;

import kr.co.healthcare.R;


public class Game2Activity extends AppCompatActivity{

    //tv_answer, tv_equal, tv_result 주석처리함
    TextView tv_level;
    TextView tv_score;
    TextView tv_question;
    TextView tv_timer;
    Button btn_opt1, btn_opt2, btn_opt3, btn_opt4;
    ProgressBar progressBar;

    int[] opt = new int[4];

    //점수, 게임 횟수, 사용자가 누른 번호, 게임1 정답 값, 게임2 연산 결과, 정답 보기 번호
    static int SCORE=0, CNT=0, CHECKED=0, LEVEL, ANS_NUM =0;
    static int ANSWER_V1=0;
    static int RESULT_V2, OPERATOR_NUM;
    static int STEP1=5, STEP2=9;                        //레벨 내 문제유형 반복 횟수 (4, 7)
    static boolean OPERATOR = false;
    static String TOTAL_TIME = "0101";                  //타이머 돌릴 시간(분-- 초--)

    CountDownTimer CDT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2);

        tv_level = findViewById(R.id.tv_level);
        LEVEL = getIntent().getIntExtra("level", -1);
        //show_level(level);

        tv_score = findViewById(R.id.tv_score);
        tv_question = findViewById(R.id.tv_question);
        btn_opt1 = findViewById(R.id.btn_opt1);
        btn_opt2 = findViewById(R.id.btn_opt2);
        btn_opt3 = findViewById(R.id.btn_opt3);
        btn_opt4 = findViewById(R.id.btn_opt4);
        progressBar = findViewById(R.id.progressBar);

        //타이머
        countDown(TOTAL_TIME);

        initialized();

        btn_opt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_method(0);
            }
        });
        btn_opt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_method(1);
            }
        });
        btn_opt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_method(2);
            }
        });
        btn_opt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_method(3);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // AlertDialog 빌더를 이용해 종료시 발생시킬 창을 띄운다
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setMessage("종료 시 점수가 저장되지 않습니다.");

        alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                initialize_data();

                //타이머 종료
                CDT.cancel();

                //게임이 실행되던 액티비티 종료
                finish();
            }
        });

        alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        alBuilder.setTitle("게임 종료");
        alBuilder.show(); //AlertDialog.Bulider로 만든 AlertDialog 보여줌
    }

    @Override
    public void onResume() {
        super.onResume();
        //initialized();
    }


    //게임 화면 초기화
    void initialized(){
        CNT++;
        tv_score.setText(SCORE +"점");
        //tv_answer.setText("");
        //tv_result.setText("");

        show_level(LEVEL);
        ANS_NUM = start_game(LEVEL);
    }

    //정보 초기화
    void initialize_data(){
        CNT =0;
        SCORE =0;
        TOTAL_TIME ="0101";
    }

    //타이머
    public void countDown(String time) {

        long conversionTime = 0;
        //1000 단위가 1초, 60000 단위가 1분, 60000 * 3600 = 1시간

        String getMin = time.substring(0, 2);
        String getSecond = time.substring(2, 4);

        // "00"이 아니고, 첫번째 자리가 0 이면 제거
        if (getMin.substring(0, 1)=="0") getMin = getMin.substring(1, 2);
        if (getSecond.substring(0, 1)=="0") getSecond = getSecond.substring(1, 2);

        // 변환시간
        conversionTime = Long.valueOf(getMin)*60*1000 + Long.valueOf(getSecond)*1000;

        //첫번째 인자 : 원하는 시간 (예를들어 30초면 30 x 1000(주기))
        //두번째 인자 : 주기(1000 = 1초)
        CDT = new CountDownTimer(conversionTime, 1000) {
            //타이머에 보이는 시간 변경
            public void onTick(long millisUntilFinished) {
                //분단위
                long getMin = millisUntilFinished - (millisUntilFinished / (60 * 60 * 1000)) ;
                String min = String.valueOf(getMin / (60 * 1000)); // 몫

                //초단위
                String second = String.valueOf((getMin % (60 * 1000)) / 1000); // 나머지

                //밀리세컨드 단위
                String millis = String.valueOf((getMin % (60 * 1000)) % 1000); // 몫

                //타이머 bar
                if(Integer.parseInt(min)==1)
                    progressBar.setProgress(60);
                else
                    progressBar.setProgress(Integer.parseInt(second));


                //숫자가 한 자리면 앞에 0을 붙임
                if (min.length()==1) {
                    min = "0" + min;
                }
                if (second.length() == 1) second = "0" + second;

                //1분 대신 60초로 표현
                if (min.equals("01")) {
                    second = "60";
                }

                //tv_timer.setText(min + ":" + second);     //분:초 타이머
                //tv_timer.setText(second);                   //초 타이머
                TOTAL_TIME = min+second;
            }

            //제한시간 종료시
            public void onFinish() {
                btn_opt1.setEnabled(false);
                btn_opt2.setEnabled(false);
                btn_opt3.setEnabled(false);
                btn_opt4.setEnabled(false);

                //tv_timer.setText("시간 종료!");

                after_time_over();
            }
        };

        CDT.start();
    }

    void after_time_over(){
        btn_opt1.setEnabled(false);
        btn_opt2.setEnabled(false);
        btn_opt3.setEnabled(false);
        btn_opt4.setEnabled(false);

        //tv_timer.setText("시간 종료!");

        //1초 지연 후 결과 페이지로 이동
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable()  {
            public void run() {
                int level = getIntent().getIntExtra("level", -1);
                int score2 = SCORE;
                initialize_data();

                Intent intent = new Intent(getApplicationContext(), GameResultActivity.class);
                intent.putExtra("score", score2);
                intent.putExtra("level", level);
                startActivity(intent);

                //화면 전환 효과 없애기
                overridePendingTransition(0, 0);
                ActivityCompat.finishAffinity(Game2Activity.this);
            }
        }, 1000); // 1초후
    }



    void set_v1_numbers(int max1, int min1, int max2, int min2, String operator){
        Random rnd = new Random();

        int q1 = rnd.nextInt(max1-min1+1) + min1;
        int q2 = rnd.nextInt(max2-min2+1) + min2;

        set_gamePage(q1, q2, operator);
    }

    void set_v1_numbers(int max, int mid, int min, String operator){
        set_v1_numbers(max, mid, mid, min, operator);
    }

    //자릿수
    void set_v1_numbers(int digit1, int digit2, String operator){
        Random rnd = new Random();

        int q1 = (int)(rnd.nextInt((int)Math.pow(10, digit1)));
        int q2 = (int)(rnd.nextInt((int)Math.pow(10, digit2)));
        while(q1<q2)
            q2 = (int)(rnd.nextInt((int)Math.pow(10, digit2)));

        set_gamePage(q1, q2, operator);
    }

    void set_gamePage(int q1, int q2, String operator){
        String ope = operator;
        Random rnd = new Random();

        if(operator.length() != 1){
            if(rnd.nextBoolean()) ope = operator.substring(0, 1);
            else ope = operator.substring(1);
        }

        if(ope.equals("*")) ope="×";
        else if(ope.equals("/")) ope="÷";

        ANSWER_V1 = calculate(q1, q2, ope);
        tv_question.setText(q1 + " " + ope + " " + q2);
    }



    int calculate(int num1, int num2, String operator){
        if(operator.equals("+")) return num1+num2;
        else if(operator.equals("-")) return num1-num2;
        else if(operator.equals("*") || operator.equals("×")) return num1*num2;
        else if(operator.equals("/") || operator.equals("÷")) return num1/num2;
        else return -1;
    }




    //레벨별 게임 함수
    int lv1_1(){
        //한자리 + 한자리
        OPERATOR = false;
        set_v1_numbers(1, 1, "+");


        //q1과 q2가 둘 다 2이면 안됨 (2+2 = 2*2)
        /*
        if(q1==2)
            while(q2==2)
                q2 = (int)(Math.random()*10);
         */

        int num = fill_opt_num(ANSWER_V1);
        return num; //정답 보기 번호
    }

    int lv1_2(){
        //두자리 +- 한자리
        OPERATOR = false;

        set_v1_numbers(2, 1, "+-");

        int num = fill_opt_num(ANSWER_V1);
        return num;
    }

    String change_operator_intToString(int operator){
        switch (operator) {
            case 0:
                return "+";
            case 1:
                return "-";
            case 2:
                return "*";
            case 3:
                return "/";
            default:
                return "";
        }
    }

    int change_operator_StringToInt(String operator){
        switch (operator) {
            case "+":
                return 0;
            case "-":
                return 1;
            case "*":
            case "×":
                return 2;
            case "/":
            case "÷":
                return 3;
            default:
                return -1;
        }
    }

    void lv1_3(){
        //한자리 [ ] 한자리 = 답
        OPERATOR = true;

        int operator = (int)(Math.random()*4);     //숫자 순서대로 + - * /
        set_v2_quiz(20, 3, 10, 2, operator);

        fill_opt_op();
    }

    int lv2_1(){
        //두자리 + 두자리
        OPERATOR = false;

        set_v1_numbers(10, 30, 70, "+");



        int num = fill_opt_num(ANSWER_V1);
        return num;
    }

    int lv2_2(){
        //세자리 - 한자리
        OPERATOR = false;

        set_v1_numbers(1, 10, 100, 1000, "-");



        int num = fill_opt_num(ANSWER_V1);
        return num;
    }

    void set_v2_quiz(int max1, int min1, int max2, int min2, String operator){
        String ope = operator;
        Random rnd = new Random();

        if(operator.length() != 1){
            if(rnd.nextBoolean()) ope = operator.substring(0, 1);
            else ope = operator.substring(1);
        }

        set_v2_quiz(max1, min1, max2, min2, change_operator_StringToInt(ope));
    }

    void set_v2_quiz(int max1, int min1, int max2, int min2, int operator){
        //한자리 [ ] 한자리 = 답
        Random rnd = new Random();
        int ope = operator;

        //Random.nextInt( <큰수> - <작은수> + 1) + <작은수>;
        int q1 = rnd.nextInt(max1-min1+1)+min1;
        int q2 = rnd.nextInt(max2-min2+1)+min2;
        while(q2>q1) q2 = rnd.nextInt(max2-min2+1)+min2;

        if(!(q2!=0 && q1%q2==0 && ope==3))
            ope = (int) (Math.random() * 3);

        set_gamePage(q1, q2, ope);
    }

    void set_gamePage(int q1, int q2, int operator){
        Random rnd = new Random();
        String ope = change_operator_intToString(operator);

        if(ope.equals("*")) ope="×";
        else if(ope.equals("/")) ope="÷";

        RESULT_V2 = calculate(q1, q2, ope);
        tv_question.setText(q1 + " □ " + q2 +"\n= "+RESULT_V2);

        OPERATOR_NUM = operator;
    }

    void lv2_3(){
        //두자리 [더하기,나누기] 두자리 = 답
        //세자리 [더하기,나누기] 한자리 = 답
        OPERATOR = true;
        Random rnd = new Random();

        //두자리_두자리
        if(rnd.nextBoolean())
            set_v2_quiz(70, 30, 30, 10, "+/");

        //세자리_한자리
        else
            set_v2_quiz(1000, 100, 10, 1, "+/");


        //tv_equal.setText("");
        fill_opt_op();
    }

    int lv3_1(){
        //두자리 +- 한자리
        OPERATOR = false;
        set_v1_numbers(70, 30, 10, "+-");


        int num = fill_opt_num(ANSWER_V1);
        return num;
    }

    void lv3_2(){
        //두자리 [곱하기, 나누기] 한자리 = 답
        OPERATOR = true;
        Random rnd = new Random();

        //두자리_두자리
        set_v2_quiz(70, 30, 10, 2, "*/");

        //tv_equal.setText("");
        fill_opt_op();
    }

    int lv3_3(){
        //두자리 + 두자리
        OPERATOR = false;

        set_v1_numbers(100, 10, 1, "+");


        int num = fill_opt_num(ANSWER_V1);
        return num;
    }


    /*
    //빈칸 채우기 문제 내기
    int[] play_fill_blanks(int q1, int q2, int rand){
        int number=0, result=-1;
        int[] return_value = new int[2];


        if(q2!=0 && q1%q2==0 && rand==3){
            result = q1 / q2;
            number = 4;
        }
        else rand = (int) (Math.random() * 3);

        if(rand==0){
            result = q1 + q2;
            number=1;
        }

        else if(rand==1){
            result = q1 - q2;
            number=2;
        }

        else if(rand==2){
            result = q1 * q2;
            number=3;
        }

        return_value[0] = number;
        return_value[1] = result;

        return return_value;
    }

    //빈칸 채우기 문제 내기
    int[] play_fill_blanks_lv2(int q1, int q2, int rand){
        int number=0, q3=-1;
        int[] return_value = new int[2];

        //나눗셈의 경우
        if(rand==0){
            q3 = q1 + q2;
            number=1;
        }

        else if(rand==1){
            q3 = q1 - q2;
            number=2;
        }

        return_value[0] = number;
        return_value[1] = q3;

        return return_value;
    }

     */

    //정답 외 보기(숫자) 채우는 함수
    int fill_opt_num(int answer){
        int rand1, rand2;

        //보기에 들어갈 수 저장
        for(int i=0; i<4; i++){
            rand1 = (int)(Math.random()*5)+1;   //정답과 1~5 차이나는 수를 위한 난수
            rand2 = (int)(Math.random()*2);     //정답보다 작은 수 만들지 큰 수 만들지 결정하는 난수

            if(rand2==0) opt[i] = answer+rand1;
            else opt[i] = answer-rand1;

            for(int j=0; j<i; j++)
                if(opt[i]==opt[j])
                    i--;
        }

        btn_opt1.setText(""+opt[0]);
        btn_opt2.setText(""+opt[1]);
        btn_opt3.setText(""+opt[2]);
        btn_opt4.setText(""+opt[3]);

        int number = fill_answer(answer);
        return number;
    }

    //정답 외 보기(연산자) 채우는 함수
    void fill_opt_op(){
        btn_opt1.setText("+");
        btn_opt2.setText("-");
        btn_opt3.setText("×");
        btn_opt4.setText("÷");
    }

    //보기에 정답을 쓰고 보기 번호 반환
    int fill_answer(int answer){
        int num = (int)(Math.random()*4);
        if(num==0){
            btn_opt1.setText(""+answer);
            return num;
        }
        else if(num==1){
            btn_opt2.setText(""+answer);
            return num;
        }

        else if(num==2){
            btn_opt3.setText(""+answer);
            return num;
        }
        else{
            btn_opt4.setText(""+answer);
            return num;
        }
    }

    //버튼 실행시
    void btn_method(int number){
        //사용자가 누른 번호(number) checked에 저장
        CHECKED = number;

        /*
        //화면에 사용자가 누른 '보기' 보여주기
        if(OPERATOR ==true){
            if(number==1) tv_answer.setText("+");
            else if(number==2) tv_answer.setText("-");
            else if(number==3) tv_answer.setText("×");
            else tv_answer.setText("÷");
        }
        else
            tv_answer.setText(""+opt[number-1]);
         */

        check_answer();
    }

    //답 확인
    void check_answer(){
        if (ANS_NUM == CHECKED){
            //tv_result.setText("정답입니다");
            /*
            if(OPERATOR ==false){
                tv_answer.setText(""+ A);
            }
            tv_answer.setTextColor(Color.parseColor("#4CAF50"));
             */
            SCORE += 200;
            tv_score.setText(SCORE +"점");

            //정답 맞으면 3초 추가
            //change_time(3);
        }
        else {
            //tv_result.setText("틀렸습니다");
            //tv_answer.setTextColor(Color.parseColor("#FF0000"));

            if(SCORE>0)
                SCORE -= 100;
            tv_score.setText(SCORE +"점");

            //정답 틀리면 5초 감소
            //change_time(-5);
        }
        OPERATOR =false;
        next_lv();
    }


    //레벨별 다른 함수를 실행
    int start_game(int level){
        int number;
        if(level==1){
            if(CNT<STEP1) number=lv1_1();
            else if(CNT<STEP2) number=lv1_2();
            else {
                lv1_3();
                number = OPERATOR_NUM;
            }
        }
        else if(level==2){
            if(CNT<STEP1) number=lv2_1();
            else if(CNT<STEP2) number=lv2_2();
            else {
                lv2_3();
                number = OPERATOR_NUM;
            }
        }
        /*
        else{
            if(CNT<STEP1) number=lv3_1();
            else if(CNT<STEP2) number=lv3_2();
            else number=lv3_3();
        }
         */
        else{
            lv3_2();
            number=OPERATOR_NUM;
            btn_opt1.setText(number+"");
        }
        //정답 반환
        return number;
    }

    //레벨 표시
    void show_level(int level){
        if(level==1) tv_level.setText("쉬움");
        else if(level==2) tv_level.setText("중간");
        else tv_level.setText("어려움");
    }

    //다음 단계로 넘어가는 메소드
    void next_lv(){
        //버튼 비활성화
        btn_opt1.setEnabled(false);
        btn_opt2.setEnabled(false);
        btn_opt3.setEnabled(false);
        btn_opt4.setEnabled(false);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable()  {
            public void run() {
                //시간이 끝나기 전까지 액티비티 반복

                btn_opt1.setEnabled(true);
                btn_opt2.setEnabled(true);
                btn_opt3.setEnabled(true);
                btn_opt4.setEnabled(true);

                initialized();
            }
        }, 1000); // 1초후
    }
}