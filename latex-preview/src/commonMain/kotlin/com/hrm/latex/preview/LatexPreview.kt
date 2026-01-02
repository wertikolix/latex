package com.hrm.latex.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hrm.latex.renderer.Latex
import org.jetbrains.compose.ui.tooling.preview.Preview

// ========== 1. 基础级别 ==========

@Preview
@Composable
fun Preview_01_SimpleText() {
    PreviewCard("01. 简单文本") {
        Latex(latex = "Hello LaTeX", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_02_SimpleSuperscript() {
    PreviewCard("02. 简单上标") {
        Latex(latex = "x^2", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_03_SimpleSubscript() {
    PreviewCard("03. 简单下标") {
        Latex(latex = "a_i", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_04_SuperAndSubscript() {
    PreviewCard("04. 上标+下标") {
        Latex(latex = "x_i^2", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_05_SimpleFraction() {
    PreviewCard("05. 简单分数") {
        Latex(latex = "\\frac{1}{2}", isDarkTheme = false)
    }
}

// ========== 2. 初级级别 ==========

@Preview
@Composable
fun Preview_06_Polynomial() {
    PreviewCard("06. 多项式") {
        Latex(latex = "ax^2 + bx + c = 0", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_07_PythagoreanTheorem() {
    PreviewCard("07. 勾股定理") {
        Latex(latex = "a^2 + b^2 = c^2", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_08_QuadraticFormula() {
    PreviewCard("08. 二次方程解") {
        Latex(latex = "x = \\frac{-b \\pm \\sqrt{b^2 - 4ac}}{2a}", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_09_SimpleSum() {
    PreviewCard("09. 简单求和") {
        Latex(latex = "\\sum_{i=1}^{n} i", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_10_SimpleIntegral() {
    PreviewCard("10. 简单积分") {
        Latex(latex = "\\int_0^1 x dx", isDarkTheme = false)
    }
}

// ========== 3. 中级级别 ==========

@Preview
@Composable
fun Preview_11_NestedFractions() {
    PreviewCard("11. 嵌套分数") {
        Latex(latex = "\\frac{1}{2 + \\frac{1}{3}}", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_12_ComplexFraction() {
    PreviewCard("12. 复杂分数") {
        Latex(latex = "\\frac{a + b}{c + d}", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_13_SquareRoot() {
    PreviewCard("13. 平方根") {
        Latex(latex = "\\sqrt{x^2 + y^2}", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_14_ComplexSum() {
    PreviewCard("14. 复杂求和") {
        Latex(latex = "\\sum_{i=1}^{n} i^2 = \\frac{n(n+1)(2n+1)}{6}", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_15_DefiniteIntegral() {
    PreviewCard("15. 定积分") {
        Latex(latex = "\\int_{0}^{\\infty} e^{-x} dx = 1", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_16_Product() {
    PreviewCard("16. 连乘") {
        Latex(latex = "\\prod_{i=1}^{n} x_i", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_17_Limit() {
    PreviewCard("17. 极限") {
        Latex(latex = "\\lim_{x \\to 0} \\frac{\\sin x}{x} = 1", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_18_Derivative() {
    PreviewCard("18. 导数") {
        Latex(latex = "\\frac{d}{dx}(x^n) = nx^{n-1}", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_18b_DerivativeWithLeft() {
    PreviewCard("18b. 导数 (使用 \\left \\right)") {
        Latex(latex = "\\frac{d}{dx}\\left(x^n\\right) = nx^{n-1}", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_Debug_Parentheses() {
    PreviewCard("Debug: 括号测试") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Latex(latex = "(x)", isDarkTheme = false)
            Latex(latex = "(x^2)", isDarkTheme = false)
            Latex(latex = "(x^{10})", isDarkTheme = false)
            Latex(latex = "\\left(x^{10}\\right)", isDarkTheme = false)
        }
    }
}

@Preview
@Composable
fun Preview_Debug_TextInSubscript() {
    PreviewCard("Debug: 下标中的文本测试") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Latex(latex = "\\prod_{p}", isDarkTheme = false)
            Latex(latex = "\\prod_{p prime}", isDarkTheme = false)
            Latex(latex = "\\prod_{p \\text{ prime}}", isDarkTheme = false)
            Latex(latex = "\\prod_{p \\text{ is prime}}", isDarkTheme = false)
            Latex(latex = "\\sum_{i \\text{ even}}", isDarkTheme = false)
        }
    }
}

// ========== 4. 高级级别 ==========

@Preview
@Composable
fun Preview_19_ContinuedFraction() {
    PreviewCard("19. 连分数") {
        Latex(latex = "1 + \\frac{1}{1 + \\frac{1}{1 + \\frac{1}{1 + x}}}", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_20_ComplexExponent() {
    PreviewCard("20. 复杂指数") {
        Latex(latex = "e^{i\\pi} + 1 = 0", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_21_NestedRadicals() {
    PreviewCard("21. 嵌套根式") {
        Latex(latex = "\\sqrt{1 + \\sqrt{1 + \\sqrt{1 + x}}}", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_22_MatrixDeterminant() {
    PreviewCard("22. 行列式表示") {
        Latex(
            latex = "\\det(A) = \\sum_{\\sigma} \\text{sgn}(\\sigma) \\prod_{i=1}^{n} a_{i,\\sigma(i)}",
            isDarkTheme = false
        )
    }
}

@Preview
@Composable
fun Preview_23_DoubleIntegral() {
    PreviewCard("23. 二重积分") {
        Latex(latex = "\\int_{0}^{1} \\int_{0}^{1} x^2 + y^2 dx dy", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_24_TaylorSeries() {
    PreviewCard("24. 泰勒级数") {
        Latex(
            latex = "f(x) = \\sum_{n=0}^{\\infty} \\frac{f^{(n)}(a)}{n!}(x-a)^n",
            isDarkTheme = false
        )
    }
}

@Preview
@Composable
fun Preview_25_ComplexSum() {
    PreviewCard("25. 复杂求和") {
        Latex(latex = "\\sum_{k=1}^{n} \\frac{1}{k^2} = \\frac{\\pi^2}{6}", isDarkTheme = false)
    }
}

// ========== 5. 专家级别 ==========

@Preview
@Composable
fun Preview_26_CauchyIntegral() {
    PreviewCard("26. 柯西积分公式") {
        Latex(
            latex = "f(z) = \\frac{1}{2\\pi i} \\oint_{\\gamma} \\frac{f(\\zeta)}{\\zeta - z} d\\zeta",
            isDarkTheme = false
        )
    }
}

@Preview
@Composable
fun Preview_27_FourierTransform() {
    PreviewCard("27. 傅里叶变换") {
        Latex(
            latex = "F(\\omega) = \\int_{-\\infty}^{\\infty} f(t) e^{-i\\omega t} dt",
            isDarkTheme = false
        )
    }
}

@Preview
@Composable
fun Preview_28_GaussianIntegral() {
    PreviewCard("28. 高斯积分") {
        Latex(latex = "\\int_{-\\infty}^{\\infty} e^{-x^2} dx = \\sqrt{\\pi}", isDarkTheme = false)
    }
}

@Preview
@Composable
fun Preview_29_RiemannZeta() {
    PreviewCard("29. 黎曼ζ函数") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // 版本1: 最简洁，约定俗成的写法
            Latex(
                latex = "\\zeta(s) = \\sum_{n=1}^{\\infty} \\frac{1}{n^s} = \\prod_{p} \\frac{1}{1-p^{-s}}",
                isDarkTheme = false
            )
            // 版本2: 使用文本模式
            Latex(
                latex = "\\zeta(s) = \\prod_{p \\text{ prime}} \\frac{1}{1-p^{-s}}",
                isDarkTheme = false
            )
        }
    }
}

@Preview
@Composable
fun Preview_30_StokesTheorem() {
    PreviewCard("30. 斯托克斯定理") {
        Latex(
            latex = "\\int_{\\partial \\Omega} \\omega = \\int_{\\Omega} d\\omega",
            isDarkTheme = false
        )
    }
}

// ========== 6. 极其复杂级别 ==========

@Preview
@Composable
fun Preview_31_ComplexNestedFraction() {
    PreviewCard("31. 超级连分数") {
        Latex(
            latex = "\\frac{1}{a + \\frac{b}{c + \\frac{d}{e + \\frac{f}{g + \\frac{h}{i + j}}}}}",
            isDarkTheme = false
        )
    }
}

@Preview
@Composable
fun Preview_32_DeepNestedRadicals() {
    PreviewCard("32. 深度嵌套根式") {
        Latex(
            latex = "\\sqrt{x + \\sqrt{y + \\sqrt{z + \\sqrt{w + \\sqrt{v + u}}}}}",
            isDarkTheme = false
        )
    }
}

@Preview
@Composable
fun Preview_33_ComplexMultiLevel() {
    PreviewCard("33. 多层次混合") {
        Latex(
            latex = "\\sum_{n=1}^{\\infty} \\frac{(-1)^n}{n} \\int_0^1 x^n \\left(\\frac{1}{1+x^2}\\right)^{\\frac{1}{2}} dx",
            isDarkTheme = false
        )
    }
}

@Preview
@Composable
fun Preview_34_SchrodingerEquation() {
    PreviewCard("34. 薛定谔方程") {
        Latex(
            latex = "i\\hbar\\frac{\\partial}{\\partial t}\\Psi(\\vec{r},t) = \\left[-\\frac{\\hbar^2}{2m}\\nabla^2 + V(\\vec{r},t)\\right]\\Psi(\\vec{r},t)",
            isDarkTheme = false
        )
    }
}

@Preview
@Composable
fun Preview_35_PathIntegral() {
    PreviewCard("35. 路径积分") {
        Latex(
            latex = "\\langle x_f | e^{-iHt/\\hbar} | x_i \\rangle = \\int \\mathcal{D}[x(t)] e^{iS[x]/\\hbar}",
            isDarkTheme = false
        )
    }
}

@Preview
@Composable
fun Preview_36_EinsteinFieldEquations() {
    PreviewCard("36. 爱因斯坦场方程") {
        Latex(
            latex = "R_{\\mu\\nu} - \\frac{1}{2}Rg_{\\mu\\nu} + \\Lambda g_{\\mu\\nu} = \\frac{8\\pi G}{c^4}T_{\\mu\\nu}",
            isDarkTheme = false
        )
    }
}

@Preview
@Composable
fun Preview_37_PartitionFunction() {
    PreviewCard("37. 配分函数") {
        Latex(
            latex = "Z = \\sum_{n=0}^{\\infty} e^{-\\beta E_n} = \\text{Tr}\\left(e^{-\\beta \\hat{H}}\\right)",
            isDarkTheme = false
        )
    }
}

@Preview
@Composable
fun Preview_38_FeynmanDiagram() {
    PreviewCard("38. 费曼传播子") {
        Latex(
            latex = "G(x-y) = \\int \\frac{d^4p}{(2\\pi)^4} \\frac{e^{-ip(x-y)}}{p^2 - m^2 + i\\epsilon}",
            isDarkTheme = false
        )
    }
}

@Preview
@Composable
fun Preview_39_YangMillsLagrangian() {
    PreviewCard("39. 杨-米尔斯拉氏量") {
        Latex(
            latex = "\\mathcal{L} = -\\frac{1}{4}F_{\\mu\\nu}^a F^{a\\mu\\nu} + \\bar{\\psi}(i\\gamma^\\mu D_\\mu - m)\\psi",
            isDarkTheme = false
        )
    }
}

@Preview
@Composable
fun Preview_40_UltraComplex() {
    PreviewCard("40. 终极复杂表达式") {
        Latex(
            latex = "\\sum_{n=0}^{\\infty} \\frac{1}{n!} \\int_{-\\infty}^{\\infty} \\left(\\frac{d}{dx}\\right)^n \\left[\\frac{\\sqrt{\\pi}}{\\sqrt{1+x^2}} \\cdot e^{-\\frac{x^2}{2\\sigma^2}} \\cdot \\prod_{k=1}^{n} \\left(1 + \\frac{x^k}{k!}\\right)\\right] dx",
            isDarkTheme = false
        )
    }
}

// ========== 所有预览合集 ==========

@Composable
fun LatexPreview() {
    PreviewAll()
}

@Preview
@Composable
fun PreviewAll() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("LaTeX 渲染测试集合", style = MaterialTheme.typography.headlineMedium)
        }

        // ========== 1. 基础级别 ==========
        item {
            Text("=== 基础级别 ===", style = MaterialTheme.typography.titleLarge)
        }
        item {
            PreviewCard("01. 简单文本") { Latex(latex = "Hello LaTeX", isDarkTheme = false) }
        }
        item {
            PreviewCard("02. 简单上标") { Latex(latex = "x^2", isDarkTheme = false) }
        }
        item {
            PreviewCard("03. 简单下标") { Latex(latex = "a_i", isDarkTheme = false) }
        }
        item {
            PreviewCard("04. 上标+下标") { Latex(latex = "x_i^2", isDarkTheme = false) }
        }
        item {
            PreviewCard("05. 简单分数") { Latex(latex = "\\frac{1}{2}", isDarkTheme = false) }
        }

        // ========== 2. 初级级别 ==========
        item {
            Text("=== 初级级别 ===", style = MaterialTheme.typography.titleLarge)
        }
        item {
            PreviewCard("06. 多项式") { Latex(latex = "ax^2 + bx + c = 0", isDarkTheme = false) }
        }
        item {
            PreviewCard("07. 勾股定理") { Latex(latex = "a^2 + b^2 = c^2", isDarkTheme = false) }
        }
        item {
            PreviewCard("08. 二次方程解") {
                Latex(
                    latex = "x = \\frac{-b \\pm \\sqrt{b^2 - 4ac}}{2a}",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("09. 简单求和") { Latex(latex = "\\sum_{i=1}^{n} i", isDarkTheme = false) }
        }
        item {
            PreviewCard("10. 简单积分") { Latex(latex = "\\int_0^1 x dx", isDarkTheme = false) }
        }

        // ========== 3. 中级级别 ==========
        item {
            Text("=== 中级级别 ===", style = MaterialTheme.typography.titleLarge)
        }
        item {
            PreviewCard("11. 嵌套分数") {
                Latex(
                    latex = "\\frac{1}{2 + \\frac{1}{3}}",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("12. 复杂分数") {
                Latex(
                    latex = "\\frac{a + b}{c + d}",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("13. 平方根") { Latex(latex = "\\sqrt{x^2 + y^2}", isDarkTheme = false) }
        }
        item {
            PreviewCard("14. 复杂求和") {
                Latex(
                    latex = "\\sum_{i=1}^{n} i^2 = \\frac{n(n+1)(2n+1)}{6}",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("15. 定积分") {
                Latex(
                    latex = "\\int_{0}^{\\infty} e^{-x} dx = 1",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("16. 连乘") { Latex(latex = "\\prod_{i=1}^{n} x_i", isDarkTheme = false) }
        }
        item {
            PreviewCard("17. 极限") {
                Latex(
                    latex = "\\lim_{x \\to 0} \\frac{\\sin x}{x} = 1",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("18. 导数") {
                Latex(
                    latex = "\\frac{d}{dx}(x^n) = nx^{n-1}",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("18b. 导数 (\\left \\right)") {
                Latex(
                    latex = "\\frac{d}{dx}\\left(x^n\\right) = nx^{n-1}",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("Debug: 括号测试") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Latex(latex = "(x)", isDarkTheme = false)
                    Latex(latex = "(x^2)", isDarkTheme = false)
                    Latex(latex = "(x^{10})", isDarkTheme = false)
                    Latex(latex = "\\left(x^{10}\\right)", isDarkTheme = false)
                }
            }
        }
        item {
            PreviewCard("Debug: 下标中的文本测试") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Latex(latex = "\\prod_{p}", isDarkTheme = false)
                    Latex(latex = "\\prod_{p prime}", isDarkTheme = false)
                    Latex(latex = "\\prod_{p \\text{ prime}}", isDarkTheme = false)
                    Latex(latex = "\\prod_{p \\text{ is prime}}", isDarkTheme = false)
                    Latex(latex = "\\sum_{i \\text{ even}}", isDarkTheme = false)
                }
            }
        }

        // ========== 4. 高级级别 ==========
        item {
            Text("=== 高级级别 ===", style = MaterialTheme.typography.titleLarge)
        }
        item {
            PreviewCard("19. 连分数") {
                Latex(
                    latex = "1 + \\frac{1}{1 + \\frac{1}{1 + \\frac{1}{1 + x}}}",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("20. 复杂指数") { Latex(latex = "e^{i\\pi} + 1 = 0", isDarkTheme = false) }
        }
        item {
            PreviewCard("21. 嵌套根式") {
                Latex(
                    latex = "\\sqrt{1 + \\sqrt{1 + \\sqrt{1 + x}}}",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("22. 行列式表示") {
                Latex(
                    latex = "\\det(A) = \\sum_{\\sigma} \\text{sgn}(\\sigma) \\prod_{i=1}^{n} a_{i,\\sigma(i)}",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("23. 二重积分") {
                Latex(
                    latex = "\\int_{0}^{1} \\int_{0}^{1} x^2 + y^2 dx dy",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("24. 泰勒级数") {
                Latex(
                    latex = "f(x) = \\sum_{n=0}^{\\infty} \\frac{f^{(n)}(a)}{n!}(x-a)^n",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("25. 复杂求和") {
                Latex(
                    latex = "\\sum_{k=1}^{n} \\frac{1}{k^2} = \\frac{\\pi^2}{6}",
                    isDarkTheme = false
                )
            }
        }

        // ========== 5. 专家级别 ==========
        item {
            Text("=== 专家级别 ===", style = MaterialTheme.typography.titleLarge)
        }
        item {
            PreviewCard("26. 柯西积分公式") {
                Latex(
                    latex = "f(z) = \\frac{1}{2\\pi i} \\oint_{\\gamma} \\frac{f(\\zeta)}{\\zeta - z} d\\zeta",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("27. 傅里叶变换") {
                Latex(
                    latex = "F(\\omega) = \\int_{-\\infty}^{\\infty} f(t) e^{-i\\omega t} dt",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("28. 高斯积分") {
                Latex(
                    latex = "\\int_{-\\infty}^{\\infty} e^{-x^2} dx = \\sqrt{\\pi}",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("29. 黎曼ζ函数") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Latex(
                        latex = "\\zeta(s) = \\sum_{n=1}^{\\infty} \\frac{1}{n^s} = \\prod_{p} \\frac{1}{1-p^{-s}}",
                        isDarkTheme = false
                    )
                    Latex(
                        latex = "\\zeta(s) = \\prod_{p \\text{ prime}} \\frac{1}{1-p^{-s}}",
                        isDarkTheme = false
                    )
                }
            }
        }
        item {
            PreviewCard("30. 斯托克斯定理") {
                Latex(
                    latex = "\\int_{\\partial \\Omega} \\omega = \\int_{\\Omega} d\\omega",
                    isDarkTheme = false
                )
            }
        }

        // ========== 6. 极其复杂级别 ==========
        item {
            Text("=== 极其复杂级别 ===", style = MaterialTheme.typography.titleLarge)
        }
        item {
            PreviewCard("31. 超级连分数") {
                Latex(
                    latex = "\\frac{1}{a + \\frac{b}{c + \\frac{d}{e + \\frac{f}{g + \\frac{h}{i + j}}}}}",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("32. 深度嵌套根式") {
                Latex(
                    latex = "\\sqrt{x + \\sqrt{y + \\sqrt{z + \\sqrt{w + \\sqrt{v + u}}}}}",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("33. 多层次混合") {
                Latex(
                    latex = "\\sum_{n=1}^{\\infty} \\frac{(-1)^n}{n} \\int_0^1 x^n \\left(\\frac{1}{1+x^2}\\right)^{\\frac{1}{2}} dx",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("34. 薛定谔方程") {
                Latex(
                    latex = "i\\hbar\\frac{\\partial}{\\partial t}\\Psi(\\vec{r},t) = \\left[-\\frac{\\hbar^2}{2m}\\nabla^2 + V(\\vec{r},t)\\right]\\Psi(\\vec{r},t)",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("35. 路径积分") {
                Latex(
                    latex = "\\langle x_f | e^{-iHt/\\hbar} | x_i \\rangle = \\int \\mathcal{D}[x(t)] e^{iS[x]/\\hbar}",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("36. 爱因斯坦场方程") {
                Latex(
                    latex = "R_{\\mu\\nu} - \\frac{1}{2}Rg_{\\mu\\nu} + \\Lambda g_{\\mu\\nu} = \\frac{8\\pi G}{c^4}T_{\\mu\\nu}",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("37. 配分函数") {
                Latex(
                    latex = "Z = \\sum_{n=0}^{\\infty} e^{-\\beta E_n} = \\text{Tr}\\left(e^{-\\beta \\hat{H}}\\right)",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("38. 费曼传播子") {
                Latex(
                    latex = "G(x-y) = \\int \\frac{d^4p}{(2\\pi)^4} \\frac{e^{-ip(x-y)}}{p^2 - m^2 + i\\epsilon}",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("39. 杨-米尔斯拉氏量") {
                Latex(
                    latex = "\\mathcal{L} = -\\frac{1}{4}F_{\\mu\\nu}^a F^{a\\mu\\nu} + \\bar{\\psi}(i\\gamma^\\mu D_\\mu - m)\\psi",
                    isDarkTheme = false
                )
            }
        }
        item {
            PreviewCard("40. 终极复杂表达式") {
                Latex(
                    latex = "\\sum_{n=0}^{\\infty} \\frac{1}{n!} \\int_{-\\infty}^{\\infty} \\left(\\frac{d}{dx}\\right)^n \\left[\\frac{\\sqrt{\\pi}}{\\sqrt{1+x^2}} \\cdot e^{-\\frac{x^2}{2\\sigma^2}} \\cdot \\prod_{k=1}^{n} \\left(1 + \\frac{x^k}{k!}\\right)\\right] dx",
                    isDarkTheme = false
                )
            }
        }
    }
}
